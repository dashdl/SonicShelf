package com.zhongxin.sonicshelf.exception;

/**
 * 自定义异常
 * 运行时异常
 */
public class CustomException extends RuntimeException {
    private String code;
    private String message;

    public CustomException(String code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public CustomException(String msg) {
        this.code = "500";
        this.message = msg;
    }

    public CustomException() {}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }
}