package com.carrental.common;

/**
 * 统一状态码定义
 */
public final class StatusCode {

    private StatusCode() {}

    // 成功
    public static final int SUCCESS = 200;

    // 客户端错误
    public static final int BAD_REQUEST = 400;
    public static final int PARAM_ERROR = 40001;       // 参数校验失败
    public static final int UNAUTHORIZED = 40100;      // Token缺失或无效
    public static final int FORBIDDEN = 40300;         // 角色权限不足
    public static final int NOT_FOUND = 40400;         // 资源不存在
    public static final int CONFLICT = 40900;          // 业务冲突（时间冲突等）
    public static final int BUSINESS_FAIL = 42200;     // 业务规则失败（信用分不足等）

    // 服务端错误
    public static final int SERVER_ERROR = 50000;      // 系统未知异常
}
