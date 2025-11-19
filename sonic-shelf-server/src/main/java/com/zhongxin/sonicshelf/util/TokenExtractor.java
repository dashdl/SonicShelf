package com.zhongxin.sonicshelf.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/**
 * Token提取工具类
 * 统一处理从HTTP请求中提取JWT token的逻辑
 * 
 * @author zhongxin
 * @since 2025
 */
public class TokenExtractor {
    
    /**
     * Authorization请求头名称
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";
    
    /**
     * Bearer token前缀
     */
    private static final String BEARER_PREFIX = "Bearer ";
    
    /**
     * 从HttpServletRequest中提取token
     * 
     * @param request HTTP请求对象
     * @return 提取到的token，如果未找到则返回null
     */
    public static String extractToken(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        return extractTokenFromHeader(authorizationHeader);
    }
    
    /**
     * 从Authorization头中提取token
     * 支持 "Bearer token" 格式
     * 
     * @param authorizationHeader Authorization头内容
     * @return 提取到的token，如果未找到或格式错误则返回null
     */
    public static String extractTokenFromHeader(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            return null;
        }
        
        // 检查是否以Bearer开头
        if (authorizationHeader.startsWith(BEARER_PREFIX)) {
            // 提取Bearer后面的token
            return authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        }
        
        // 如果不是Bearer格式，直接返回整个字符串（兼容其他格式）
        return authorizationHeader.trim();
    }
    
    /**
     * 验证token格式是否有效
     * 
     * @param token 要验证的token
     * @return true如果token格式有效，false否则
     */
    public static boolean isValidTokenFormat(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        String[] parts = token.split("\\.");
        return parts.length == 3;
    }
    
    /**
     * 安全提取token，如果提取失败返回null而不是抛出异常
     * 
     * @param request HTTP请求对象
     * @return 提取到的token或null
     */
    public static String safeExtractToken(HttpServletRequest request) {
        try {
            return extractToken(request);
        } catch (Exception e) {
            return null;
        }
    }
}