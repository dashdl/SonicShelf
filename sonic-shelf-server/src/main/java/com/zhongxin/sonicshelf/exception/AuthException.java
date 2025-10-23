package com.zhongxin.sonicshelf.exception;
/**
 * 自定义异常
 * 运行时异常
 */
public class AuthException extends RuntimeException {
    private String code;
    private String message;

    public AuthException(String code, String message) {
        this.code = code;
        this.message = message;
    }

}