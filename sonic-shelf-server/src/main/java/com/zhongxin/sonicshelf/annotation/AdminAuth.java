package com.zhongxin.sonicshelf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 管理员权限验证注解
 * 用于标记需要管理员权限的Controller方法
 * 
 * @author zhongxin
 * @since 2025
 */
@Target(ElementType.METHOD)  // 只能用于方法上
@Retention(RetentionPolicy.RUNTIME)  // 运行时有效
public @interface AdminAuth {
    // 可以添加一些属性，比如自定义错误消息
    String errorMessage() default "需要管理员权限";
}