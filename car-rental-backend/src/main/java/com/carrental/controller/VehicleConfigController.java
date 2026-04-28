package com.carrental.controller;

import com.carrental.common.Result;
import com.carrental.common.StatusCode;
import com.carrental.common.UserContext;
import com.carrental.dto.VehicleConfigVO;
import com.carrental.exception.BusinessException;
import com.carrental.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-configs")
@RequiredArgsConstructor
public class VehicleConfigController {

    private final VehicleService vehicleService;

    @GetMapping
    public Result<List<VehicleConfigVO>> list() {
        return Result.ok(vehicleService.listConfigs());
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody VehicleConfigVO vo) {
        checkAdmin();
        vehicleService.updateConfig(id, vo);
        return Result.ok("配置已更新", null);
    }

    private void checkAdmin() {
        if (!"ADMIN".equals(UserContext.getRole())) {
            throw new BusinessException(StatusCode.FORBIDDEN, "无权限，仅管理员可操作");
        }
    }
}
