package com.carrental.common;

import lombok.Data;

import java.time.Instant;

/**
 * 统一 API 响应对象
 */
@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    private Result() {
        this.timestamp = Instant.now().toEpochMilli();
    }

    private Result(int code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 成功响应 ====================

    public static <T> Result<T> ok() {
        return new Result<>(StatusCode.SUCCESS, "success", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(StatusCode.SUCCESS, "success", data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(StatusCode.SUCCESS, message, data);
    }

    // ==================== 失败响应 ====================

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(StatusCode.BUSINESS_FAIL, message, null);
    }

    // ==================== 常用快捷方法 ====================

    public static <T> Result<T> paramError(String message) {
        return fail(StatusCode.PARAM_ERROR, message);
    }

    public static <T> Result<T> unauthorized(String message) {
        return fail(StatusCode.UNAUTHORIZED, message);
    }

    public static <T> Result<T> forbidden(String message) {
        return fail(StatusCode.FORBIDDEN, message);
    }

    public static <T> Result<T> notFound(String message) {
        return fail(StatusCode.NOT_FOUND, message);
    }

    public static <T> Result<T> conflict(String message) {
        return fail(StatusCode.CONFLICT, message);
    }

    public static <T> Result<T> businessFail(String message) {
        return fail(StatusCode.BUSINESS_FAIL, message);
    }

    public static <T> Result<T> serverError(String message) {
        return fail(StatusCode.SERVER_ERROR, message);
    }
}
