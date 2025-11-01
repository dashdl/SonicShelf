package com.zhongxin.sonicshelf.util;

import lombok.Data;

@Data
public class Result {
    private String code;
    private String message;
    private Object data;

    public static Result success() {
        Result result = new Result();
        result.setCode("200");
        result.setMessage("请求成功");
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setCode("200");
        result.setData(data);
        result.setMessage("请求成功");
        return result;
    }

    public static Result success(String message) {
        Result result = new Result();
        result.setCode("200");
        result.setMessage(message);
        return result;
    }

    public static Result success(String message,Object data) {
        Result result = new Result();
        result.setCode("200");
        result.setData(data);
        result.setMessage(message);
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.setCode("500");
        result.setMessage(msg);
        return result;
    }

    public static Result error(String code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(msg);
        return result;
    }
}
