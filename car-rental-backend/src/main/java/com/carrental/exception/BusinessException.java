package com.carrental.exception;

import com.carrental.common.StatusCode;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = StatusCode.BUSINESS_FAIL;
    }

    /**
     * 资源不存在
     */
    public static BusinessException notFound(String message) {
        return new BusinessException(StatusCode.NOT_FOUND, message);
    }

    /**
     * 业务冲突（如时间冲突）
     */
    public static BusinessException conflict(String message) {
        return new BusinessException(StatusCode.CONFLICT, message);
    }

    /**
     * 业务规则失败（如信用分不足）
     */
    public static BusinessException businessFail(String message) {
        return new BusinessException(StatusCode.BUSINESS_FAIL, message);
    }

    /**
     * 参数错误
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(StatusCode.PARAM_ERROR, message);
    }
}
