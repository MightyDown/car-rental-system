package com.carrental.controller;

import com.carrental.common.PageResult;
import com.carrental.common.Result;
import com.carrental.common.StatusCode;
import com.carrental.common.UserContext;
import com.carrental.dto.UpdateMeDTO;
import com.carrental.dto.UserUpdateDTO;
import com.carrental.dto.UserVO;
import com.carrental.exception.BusinessException;
import com.carrental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Result<PageResult<UserVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        checkAdmin();
        return Result.ok(userService.listUsers(page, size, keyword));
    }

    @GetMapping("/me")
    public Result<UserVO> me() {
        Long userId = UserContext.getUserId();
        return Result.ok(userService.getMe(userId));
    }

    @PutMapping("/me")
    public Result<UserVO> updateMe(@Valid @RequestBody UpdateMeDTO dto) {
        Long userId = UserContext.getUserId();
        return Result.ok("个人信息已更新", userService.updateMe(userId, dto));
    }

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        checkAdmin();
        return Result.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        checkAdmin();
        userService.updateUser(id, dto);
        return Result.ok("更新成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        checkAdmin();
        if (status != 0 && status != 1) {
            return Result.paramError("状态值必须为0或1");
        }
        if (id.equals(UserContext.getUserId())) {
            return Result.fail(StatusCode.BUSINESS_FAIL, "不能修改自己的账号状态");
        }
        userService.updateUserStatus(id, status);
        return Result.ok(status == 1 ? "已启用" : "已禁用", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkAdmin();
        if (id.equals(UserContext.getUserId())) {
            return Result.fail(StatusCode.BUSINESS_FAIL, "不能删除自己的账号");
        }
        userService.deleteUser(id);
        return Result.ok("删除成功", null);
    }

    private void checkAdmin() {
        if (!"ADMIN".equals(UserContext.getRole())) {
            throw new BusinessException(StatusCode.FORBIDDEN, "无权限，仅管理员可操作");
        }
    }
}
