package com.carrental.controller;

import com.carrental.common.Result;
import com.carrental.common.StatusCode;
import com.carrental.common.UserContext;
import com.carrental.dto.BookingVO;
import com.carrental.dto.PickupDTO;
import com.carrental.dto.ReturnDTO;
import com.carrental.exception.BusinessException;
import com.carrental.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rental")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("/pickup")
    public Result<BookingVO> pickup(@Valid @RequestBody PickupDTO dto) {
        checkAdmin();
        BookingVO vo = rentalService.pickup(dto);
        return Result.ok("取车成功", vo);
    }

    @PostMapping("/return")
    public Result<BookingVO> returnVehicle(@Valid @RequestBody ReturnDTO dto) {
        checkAdmin();
        BookingVO vo = rentalService.returnVehicle(dto);
        return Result.ok("还车成功，费用已自动计算", vo);
    }

    private void checkAdmin() {
        if (!"ADMIN".equals(UserContext.getRole())) {
            throw new BusinessException(StatusCode.FORBIDDEN, "无权限，仅管理员可操作");
        }
    }
}
