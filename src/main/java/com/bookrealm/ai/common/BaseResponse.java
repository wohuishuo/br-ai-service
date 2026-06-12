package com.bookrealm.ai.common;

import java.io.Serializable;

/**
 * 通用返回结构:所有接口统一返回 {code, data, message}。
 * 照抄 user-center 的 BaseResponse。
 */
public class BaseResponse<T> implements Serializable {

    private int code;
    private T data;
    private String message;

    public BaseResponse() {
    }

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}


