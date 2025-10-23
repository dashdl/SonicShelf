package com.zhongxin.sonicshelf.exception;

/**
 * 自定义异常
 * 运行时异常
 */
public class CustomerException extends RuntimeException {
    private String code;
    private String message;

    public CustomerException(String code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public CustomerException(String msg) {
        this.code = "500";
        this.message = msg;
    }

    public CustomerException() {}

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