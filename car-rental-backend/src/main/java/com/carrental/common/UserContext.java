package com.carrental.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户上下文 —— 基于 ThreadLocal 存储当前请求的用户信息。
 * 由 JwtInterceptor 在请求进入时设置，请求结束后自动清除。
 *
 * <pre>
 *   // 下游模块使用示例：
 *   Long   userId   = UserContext.getUserId();
 *   String username = UserContext.getUsername();
 *   String role     = UserContext.getRole();
 * </pre>
 */
public final class UserContext {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<>();

    private UserContext() {}

    public static void set(Long userId, String username, String role) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("username", username);
        map.put("role", role);
        CONTEXT.set(map);
    }

    public static Long getUserId() {
        Map<String, Object> map = CONTEXT.get();
        return map != null ? (Long) map.get("userId") : null;
    }

    public static String getUsername() {
        Map<String, Object> map = CONTEXT.get();
        return map != null ? (String) map.get("username") : null;
    }

    public static String getRole() {
        Map<String, Object> map = CONTEXT.get();
        return map != null ? (String) map.get("role") : null;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
