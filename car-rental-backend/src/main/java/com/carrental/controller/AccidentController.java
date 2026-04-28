package com.carrental.controller;

import com.carrental.common.PageResult;
import com.carrental.common.Result;
import com.carrental.common.StatusCode;
import com.carrental.common.UserContext;
import com.carrental.dto.*;
import com.carrental.exception.BusinessException;
import com.carrental.service.AccidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accidents")
@RequiredArgsConstructor
public class AccidentController {

    private final AccidentService accidentService;

    @GetMapping
    public Result<PageResult<AccidentVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        checkAdmin();
        return Result.ok(accidentService.listAccidents(page, size, status));
    }

    @GetMapping("/{id}")
    public Result<AccidentVO> getById(@PathVariable Long id) {
        checkAdmin();
        return Result.ok(accidentService.getAccidentById(id));
    }

    @PostMapping
    public Result<AccidentVO> create(@Valid @RequestBody AccidentSaveDTO dto) {
        checkAdmin();
        AccidentVO vo = accidentService.createAccident(dto);
        return Result.ok("事故已登记，车辆状态已更新为维修中", vo);
    }

    @PutMapping("/{id}/process")
    public Result<Void> process(@PathVariable Long id, @Valid @RequestBody AccidentProcessDTO dto) {
        checkAdmin();
        accidentService.processAccident(id, dto);
        return Result.ok("事故已处理，信用分已扣减", null);
    }

    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        checkAdmin();
        accidentService.completeAccident(id);
        return Result.ok("维修已完成，车辆已恢复空闲", null);
    }

    private void checkAdmin() {
        if (!"ADMIN".equals(UserContext.getRole())) {
            throw new BusinessException(StatusCode.FORBIDDEN, "无权限，仅管理员可操作");
        }
    }
}
