package com.bookrealm.ai.common;

public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    UNAUTHORIZED(40100, "未登录或令牌无效"),
    FORBIDDEN(40300, "无权访问"),
    NOT_FOUND(40400, "请求数据不存在"),
    SYSTEM_ERROR(50000, "系统内部异常");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}

