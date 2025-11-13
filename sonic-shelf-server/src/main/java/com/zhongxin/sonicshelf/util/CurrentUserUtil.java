package com.zhongxin.sonicshelf.util;

import com.zhongxin.sonicshelf.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 获取当前用户的工具类
 * 提供便捷的方法来从Spring Security上下文获取当前登录用户的信息
 */
public class CurrentUserUtil {

    /**
     * 获取当前登录用户
     * @return 当前登录的用户对象
     * @throws IllegalStateException 当没有用户登录时抛出异常
     */
    public static User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof UserDetails) {
            // 如果principal是UserDetails类型，我们需要确保它能转换为User类型
            // 这取决于UserDetailsService的实现是否返回User对象
            return (User) principal;
        } else {
            throw new IllegalStateException("无法获取当前用户信息");
        }
    }
    
    /**
     * 获取当前登录用户，不抛出异常
     * @return 当前登录的用户对象，如果没有用户登录则返回null
     */
    public static User getCurrentUserOrNull() {
        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null ||
                    SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null) {
                return null;
            }
            return getCurrentUser();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 检查是否有用户登录
     * @return 如果有用户登录则返回true，否则返回false
     */
    public static boolean isLoggedIn() {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String);
    }
    
    /**
     * 获取当前登录用户的用户名
     * @return 当前登录用户的用户名
     * @throws IllegalStateException 当没有用户登录时抛出异常
     */
    public static String getCurrentUsername() {
        if (!isLoggedIn()) {
            throw new IllegalStateException("没有用户登录");
        }
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    
    /**
     * 获取当前登录用户的ID
     * @return 当前登录用户的ID
     * @throws IllegalStateException 当没有用户登录时抛出异常
     */
    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user.getId();
    }
}