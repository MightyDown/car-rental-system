package com.carrental.interceptor;

import com.carrental.common.Result;
import com.carrental.common.UserContext;
import com.carrental.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // GET requests are allowed without token for public endpoints;
            // each controller handles its own authorization via checkAdmin()
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                return true;
            }
            writeUnauthorized(response, "未登录，请先登录");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.parseToken(token);
            Number userIdNum = claims.get("userId", Number.class);
            Long userId = userIdNum != null ? userIdNum.longValue() : null;
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            UserContext.set(userId, username, role);
            return true;
        } catch (Exception e) {
            // For GET requests, an invalid token should not block the request;
            // the controller's checkAdmin() will reject if authorization is needed
            if ("GET".equalsIgnoreCase(request.getMethod())) {
                return true;
            }
            writeUnauthorized(response, "Token无效或已过期，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                Result.unauthorized(message)));
    }
}
