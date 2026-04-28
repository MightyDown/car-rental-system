package com.carrental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.common.PageResult;
import com.carrental.common.StatusCode;
import com.carrental.common.UserContext;
import com.carrental.dto.BookingSaveDTO;
import com.carrental.dto.BookingVO;
import com.carrental.entity.Booking;
import com.carrental.entity.User;
import com.carrental.entity.Vehicle;
import com.carrental.entity.VehicleConfig;
import com.carrental.exception.BusinessException;
import com.carrental.mapper.BookingMapper;
import com.carrental.mapper.UserMapper;
import com.carrental.mapper.VehicleConfigMapper;
import com.carrental.mapper.VehicleMapper;
import com.carrental.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final VehicleMapper vehicleMapper;
    private final UserMapper userMapper;
    private final VehicleConfigMapper configMapper;

    private static final Map<String, String> STATUS_NAMES = new LinkedHashMap<>();
    private static final Map<String, String> TYPE_NAMES = new LinkedHashMap<>();

    static {
        STATUS_NAMES.put("pending", "待确认");
        STATUS_NAMES.put("confirmed", "已确认");
        STATUS_NAMES.put("picked_up", "已取车");
        STATUS_NAMES.put("overdue", "超时");
        STATUS_NAMES.put("returned", "已还车");
        STATUS_NAMES.put("cancelled", "已取消");

        TYPE_NAMES.put("economy", "经济型");
        TYPE_NAMES.put("luxury", "豪华型");
    }

    @Override
    public PageResult<BookingVO> listBookings(Integer page, Integer size, String status, Long userId) {
        LambdaQueryWrapper<Booking> wrapper = new LambdaQueryWrapper<Booking>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Booking::getStatus, status);
        }
        if (userId != null) {
            wrapper.eq(Booking::getUserId, userId);
        }
        wrapper.orderByDesc(Booking::getCreateTime);

        IPage<Booking> iPage = bookingMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.from(iPage.convert(this::buildVO));
    }

    @Override
    public PageResult<BookingVO> listMyBookings(Integer page, Integer size, String status) {
        return listBookings(page, size, status, UserContext.getUserId());
    }

    @Override
    public BookingVO getBookingById(Long id) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw BusinessException.notFound("预订不存在");
        }
        return buildVO(booking);
    }

    @Override
    @Transactional
    public BookingVO createBooking(BookingSaveDTO dto) {
        Long userId = UserContext.getUserId();

        // ===== 前置检查1: 车辆存在且可用 =====
        Vehicle vehicle = vehicleMapper.selectById(dto.getVehicleId());
        if (vehicle == null) {
            throw BusinessException.notFound("车辆不存在");
        }
        if (!"idle".equals(vehicle.getStatus())) {
            throw BusinessException.businessFail("车辆当前不可用，状态：" + getStatusName(vehicle.getStatus()));
        }

        // ===== 前置检查2: 时间有效性 =====
        LocalDateTime now = LocalDateTime.now();
        if (dto.getPlannedPickupTime().isBefore(now)) {
            throw BusinessException.paramError("计划取车时间不能早于当前时间");
        }
        if (!dto.getPlannedReturnTime().isAfter(dto.getPlannedPickupTime())) {
            throw BusinessException.paramError("计划还车时间必须晚于计划取车时间");
        }

        // Calculate planned days (minimum 1 day)
        long hours = ChronoUnit.HOURS.between(dto.getPlannedPickupTime(), dto.getPlannedReturnTime());
        int plannedDays = Math.max(1, (int) Math.ceil(hours / 24.0));

        // ===== 前置检查3: 时间冲突检测（等号算冲突）=====
        List<Booking> conflicting = bookingMapper.selectList(new LambdaQueryWrapper<Booking>()
                .eq(Booking::getVehicleId, dto.getVehicleId())
                .in(Booking::getStatus, "pending", "confirmed", "picked_up", "overdue")
                .and(w -> w
                        .le(Booking::getPlannedPickupTime, dto.getPlannedReturnTime())
                        .ge(Booking::getPlannedReturnTime, dto.getPlannedPickupTime())));
        // 冲突公式：newStart <= existEnd AND existStart <= newEnd
        // LambdaWrapper写法：existStart <= newEnd AND existEnd >= newStart
        if (!conflicting.isEmpty()) {
            throw BusinessException.conflict("该时间段车辆已被预订，请选择其他时间或车辆");
        }

        // ===== 前置检查4: 用户无进行中的预订 =====
        Long activeCount = bookingMapper.selectCount(new LambdaQueryWrapper<Booking>()
                .eq(Booking::getUserId, userId)
                .in(Booking::getStatus, "pending", "confirmed", "picked_up", "overdue"));
        if (activeCount > 0) {
            throw BusinessException.businessFail("您已有进行中的预订，无法重复预订");
        }

        // ===== 前置检查5: 用户账号检查 =====
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() == 0) {
            throw BusinessException.businessFail("账号状态异常，无法预订");
        }
        if (user.getLicenseNo() == null || user.getLicenseNo().isEmpty()) {
            throw BusinessException.businessFail("驾照号为空，无法预订");
        }

        // ===== 前置检查6: 信用分检查 =====
        int creditScore = user.getCreditScore() != null ? user.getCreditScore() : 100;
        if (creditScore < 60) {
            throw new BusinessException(StatusCode.BUSINESS_FAIL,
                    "信用分低于60分，无法租车，当前信用分：" + creditScore);
        }

        // ===== 计算预计租金 =====
        VehicleConfig config = configMapper.selectOne(new LambdaQueryWrapper<VehicleConfig>()
                .eq(VehicleConfig::getType, vehicle.getType()));
        BigDecimal dailyRate = config != null ? config.getDailyRate() : BigDecimal.ZERO;
        BigDecimal estimatedRent = dailyRate.multiply(BigDecimal.valueOf(plannedDays));

        // ===== 创建预订 =====
        Booking booking = new Booking();
        booking.setBookingNo(generateBookingNo());
        booking.setUserId(userId);
        booking.setVehicleId(dto.getVehicleId());
        booking.setPlannedPickupTime(dto.getPlannedPickupTime());
        booking.setPlannedReturnTime(dto.getPlannedReturnTime());
        booking.setPlannedDays(plannedDays);
        booking.setEstimatedRent(estimatedRent);
        booking.setStatus("pending");
        bookingMapper.insert(booking);

        // ===== 更新车辆状态 =====
        vehicle.setStatus("reserved");
        vehicleMapper.updateById(vehicle);

        return buildVO(booking);
    }

    @Override
    @Transactional
    public void confirmBooking(Long id) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw BusinessException.notFound("预订不存在");
        }
        if (!"pending".equals(booking.getStatus())) {
            throw BusinessException.businessFail("当前预订状态不允许确认，状态：" + getStatusName(booking.getStatus()));
        }

        Vehicle vehicle = vehicleMapper.selectById(booking.getVehicleId());
        if (vehicle == null || !"reserved".equals(vehicle.getStatus())) {
            throw BusinessException.businessFail("车辆状态异常，无法确认预订");
        }

        booking.setStatus("confirmed");
        bookingMapper.updateById(booking);

        vehicle.setStatus("confirmed");
        vehicleMapper.updateById(vehicle);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingMapper.selectById(id);
        if (booking == null) {
            throw BusinessException.notFound("预订不存在");
        }
        if (!"pending".equals(booking.getStatus()) && !"confirmed".equals(booking.getStatus())) {
            throw BusinessException.businessFail("当前预订状态不允许取消，状态：" + getStatusName(booking.getStatus()));
        }

        booking.setStatus("cancelled");
        bookingMapper.updateById(booking);

        Vehicle vehicle = vehicleMapper.selectById(booking.getVehicleId());
        if (vehicle != null && ("reserved".equals(vehicle.getStatus()) || "confirmed".equals(vehicle.getStatus()))) {
            vehicle.setStatus("idle");
            vehicleMapper.updateById(vehicle);
        }
    }

    private String generateBookingNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%06d", new Random().nextInt(999999));
        return "BK" + date + random;
    }

    private String getStatusName(String status) {
        return STATUS_NAMES.getOrDefault(status, status);
    }

    private BookingVO buildVO(Booking b) {
        User user = b.getUserId() != null ? userMapper.selectById(b.getUserId()) : null;
        Vehicle vehicle = b.getVehicleId() != null ? vehicleMapper.selectById(b.getVehicleId()) : null;

        return BookingVO.builder()
                .id(b.getId())
                .bookingNo(b.getBookingNo())
                .userId(b.getUserId())
                .userName(user != null ? user.getUsername() : null)
                .userRealName(user != null ? user.getRealName() : null)
                .vehicleId(b.getVehicleId())
                .vehiclePlateNo(vehicle != null ? vehicle.getPlateNo() : null)
                .vehicleModel(vehicle != null ? vehicle.getModel() : null)
                .vehicleType(vehicle != null ? vehicle.getType() : null)
                .vehicleTypeName(vehicle != null ? TYPE_NAMES.getOrDefault(vehicle.getType(), "") : null)
                .plannedPickupTime(b.getPlannedPickupTime())
                .plannedReturnTime(b.getPlannedReturnTime())
                .plannedDays(b.getPlannedDays())
                .estimatedRent(b.getEstimatedRent())
                .actualPickupTime(b.getActualPickupTime())
                .pickupMileage(b.getPickupMileage())
                .actualReturnTime(b.getActualReturnTime())
                .returnMileage(b.getReturnMileage())
                .overtimeMinutes(b.getOvertimeMinutes())
                .overtimeFee(b.getOvertimeFee())
                .excessMileageFee(b.getExcessMileageFee())
                .totalFee(b.getTotalFee())
                .status(b.getStatus())
                .statusName(STATUS_NAMES.getOrDefault(b.getStatus(), b.getStatus()))
                .createTime(b.getCreateTime())
                .build();
    }
}
