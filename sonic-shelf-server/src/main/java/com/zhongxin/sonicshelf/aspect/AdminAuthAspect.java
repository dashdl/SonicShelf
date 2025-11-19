package com.zhongxin.sonicshelf.aspect;

import com.zhongxin.sonicshelf.annotation.AdminAuth;
import com.zhongxin.sonicshelf.util.JwtUtil;
import com.zhongxin.sonicshelf.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 管理员权限验证切面
 * 用于处理@AdminAuth注解的方法，进行统一的管理员权限验证
 *
 * @author zhongxin
 * @since 2025
 */
@Aspect
@Component
@Slf4j
public class AdminAuthAspect {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Pointcut("@annotation(com.zhongxin.sonicshelf.annotation.AdminAuth)")
    public void adminAuthPointcut() {
    }

    /**
     * 环绕通知：在方法执行前后进行管理员权限验证
     *
     * @param joinPoint 连接点，包含方法信息和参数
     * @return 方法执行结果或权限验证失败的响应
     * @throws Throwable 方法执行时可能抛出的异常
     */
    @Around("adminAuthPointcut()")
    public Object aroundAdminAuth(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取@AdminAuth注解
        AdminAuth adminAuth = method.getAnnotation(AdminAuth.class);
        String errorMessage = adminAuth.errorMessage();

        // 从SecurityContext获取当前认证的用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("用户未认证");
            return Result.error(errorMessage);
        }

        // 获取认证对象
        Object principal = authentication.getPrincipal();
        if (principal == null || principal instanceof String) {
            log.warn("认证信息无效");
            return Result.error(errorMessage);
        }

        // 检查用户是否为管理员
        UserDetails userDetails = (UserDetails) principal;
        if (!jwtUtil.isAdminUser(userDetails)) {
            log.warn("用户 {} 无管理员权限", userDetails.getUsername());
            return Result.error(errorMessage);
        }
        // 执行原方法
        return joinPoint.proceed();
    }

    /**
     * 从RequestContextHolder中获取HttpServletRequest对象
     * @return HttpServletRequest对象或null
     */
    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        return null;
    }
}