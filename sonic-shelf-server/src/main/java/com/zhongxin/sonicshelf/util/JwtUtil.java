package com.zhongxin.sonicshelf.util;

import com.zhongxin.sonicshelf.entity.Admin;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretString;

    private final Long expiration = 86400000L; // 24小时

    // 将字符串密钥转换为安全的SecretKey
    private SecretKey getSigningKey() {
        // 方法1：使用Keys工具类
        return Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));

        // 方法2：生成一个安全的随机密钥
        // return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        String userType = (userDetails instanceof Admin) ? "admin" : "user";
        claims.put("userType", userType);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 使用SecretKey
                .compact();
    }

    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 这里也要修改
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return username != null &&
                    username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }


    public Boolean isAdmin(String token) {
        return "admin".equals(getUserTypeFromToken(token));
    }

    /**
     * 根据UserDetails检查用户是否为管理员
     * 此方法用于在已经认证的情况下快速检查用户类型
     *
     * @param userDetails 用户详情
     * @return 如果是管理员返回true，否则返回false
     */
    public Boolean isAdminUser(UserDetails userDetails) {
        // 根据UserDetails的实际类型判断是否为管理员
        return userDetails instanceof Admin;
    }

    /**
     * 根据用户名检查用户是否为管理员（重载方法，用于向后兼容）
     * 注意：此方法可能需要从数据库查询用户信息，性能较差
     * 建议优先使用isAdminUser(UserDetails)方法
     *
     * @param username 用户名
     * @return 如果是管理员返回true，否则返回false
     */
    public Boolean isAdminUser(String username) {
        // 这里可以根据实际需求实现，例如从数据库查询用户类型
        // 由于UserDetailsService可能返回不同类型的UserDetails
        // 这里暂时返回false，实际应用中应根据业务逻辑实现
        return false;
    }

    public String getUserTypeFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userType", String.class);
        } catch (Exception e) {
            return "user";
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            final Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}