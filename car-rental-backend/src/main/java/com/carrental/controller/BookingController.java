package com.carrental.controller;

import com.carrental.common.PageResult;
import com.carrental.common.Result;
import com.carrental.common.StatusCode;
import com.carrental.common.UserContext;
import com.carrental.dto.BookingSaveDTO;
import com.carrental.dto.BookingVO;
import com.carrental.exception.BusinessException;
import com.carrental.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public Result<PageResult<BookingVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId) {
        checkAdmin();
        return Result.ok(bookingService.listBookings(page, size, status, userId));
    }

    @GetMapping("/my")
    public Result<PageResult<BookingVO>> myBookings(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        return Result.ok(bookingService.listMyBookings(page, size, status));
    }

    @GetMapping("/{id}")
    public Result<BookingVO> getById(@PathVariable Long id) {
        return Result.ok(bookingService.getBookingById(id));
    }

    @PostMapping
    public Result<BookingVO> create(@Valid @RequestBody BookingSaveDTO dto) {
        BookingVO vo = bookingService.createBooking(dto);
        return Result.ok("预订成功，等待工作人员确认", vo);
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        checkAdmin();
        bookingService.confirmBooking(id);
        return Result.ok("预订已确认", null);
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return Result.ok("预订已取消", null);
    }

    private void checkAdmin() {
        if (!"ADMIN".equals(UserContext.getRole())) {
            throw new BusinessException(StatusCode.FORBIDDEN, "无权限，仅管理员可操作");
        }
    }
}
