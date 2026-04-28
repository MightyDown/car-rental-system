package com.carrental.controller;

import com.carrental.common.Result;
import com.carrental.dto.LoginDTO;
import com.carrental.dto.LoginVO;
import com.carrental.dto.RegisterDTO;
import com.carrental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.ok("登录成功", vo);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.ok("注册成功，请等待工作人员审核", null);
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT 无状态，客户端自行清除 Token
        return Result.ok("已退出登录", null);
    }
}
