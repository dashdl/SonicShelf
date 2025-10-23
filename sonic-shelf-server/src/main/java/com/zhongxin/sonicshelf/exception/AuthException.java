package com.zhongxin.sonicshelf.exception;
/**
 * 自定义异常
 * 运行时异常
 */
public class AuthException extends RuntimeException {
    private String code;
    private String message;

    public AuthException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}