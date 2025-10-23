package com.zhongxin.sonicshelf.util;

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
        // 方法1：使用Keys工具类（推荐）
        return Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));

        // 方法2：或者生成一个安全的随机密钥（取消注释使用）
        // return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 使用SecretKey而不是字符串
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

    private boolean isTokenExpired(String token) {
        try {
            final Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 这里也要修改
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