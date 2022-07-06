package com.zcx.ymyy.entity;

import lombok.Data;

@Data
public class Result {

    private Integer code;
    private String msg;
    private Object data;
    private Integer count;

    public static Result ok(Object object, Integer count) {
        Result result = new Result();
        result.setCount(count);
        result.setData(object);
        result.setMsg("查询成功");
        result.setCode(0);
        return result;
    }

    public static Result ok(String msg) {
        Result result = new Result();
        result.setCode(0);
        result.setMsg(msg);
        result.setData(null);
        result.setCount(null);
        return result;
    }

    public static Result ok(Object object, String msg) {
        Result result = new Result();
        result.setCode(0);
        result.setMsg(msg);
        result.setData(object);
        result.setCount(null);
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.setCode(-1);
        result.setMsg(msg);
        result.setData(null);
        result.setCount(null);
        return result;
    }

}
