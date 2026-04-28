package com.carrental.controller;

import com.carrental.common.PageResult;
import com.carrental.common.Result;
import com.carrental.common.StatusCode;
import com.carrental.common.UserContext;
import com.carrental.dto.VehicleSaveDTO;
import com.carrental.dto.VehicleVO;
import com.carrental.exception.BusinessException;
import com.carrental.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public Result<PageResult<VehicleVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        return Result.ok(vehicleService.listVehicles(page, size, keyword, type, status));
    }

    @GetMapping("/{id}")
    public Result<VehicleVO> getById(@PathVariable Long id) {
        return Result.ok(vehicleService.getVehicleById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody VehicleSaveDTO dto) {
        checkAdmin();
        vehicleService.createVehicle(dto);
        return Result.ok("添加成功", null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody VehicleSaveDTO dto) {
        checkAdmin();
        vehicleService.updateVehicle(id, dto);
        return Result.ok("更新成功", null);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        checkAdmin();
        vehicleService.updateStatus(id, status);
        return Result.ok("状态已更新", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkAdmin();
        vehicleService.deleteVehicle(id);
        return Result.ok("删除成功", null);
    }

    private void checkAdmin() {
        if (!"ADMIN".equals(UserContext.getRole())) {
            throw new BusinessException(StatusCode.FORBIDDEN, "无权限，仅管理员可操作");
        }
    }
}
