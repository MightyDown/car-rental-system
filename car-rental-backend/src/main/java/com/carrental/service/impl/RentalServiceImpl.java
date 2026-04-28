package com.carrental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carrental.dto.BookingVO;
import com.carrental.dto.PickupDTO;
import com.carrental.dto.ReturnDTO;
import com.carrental.entity.Booking;
import com.carrental.entity.User;
import com.carrental.entity.Vehicle;
import com.carrental.entity.VehicleConfig;
import com.carrental.exception.BusinessException;
import com.carrental.mapper.BookingMapper;
import com.carrental.mapper.UserMapper;
import com.carrental.mapper.VehicleConfigMapper;
import com.carrental.mapper.VehicleMapper;
import com.carrental.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

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
    @Transactional
    public BookingVO pickup(PickupDTO dto) {
        Booking booking = bookingMapper.selectById(dto.getBookingId());
        if (booking == null) {
            throw BusinessException.notFound("预订不存在");
        }
        if (!"confirmed".equals(booking.getStatus())) {
            throw BusinessException.businessFail("当前预订状态不允许取车，状态：" +
                    STATUS_NAMES.getOrDefault(booking.getStatus(), booking.getStatus()));
        }

        Vehicle vehicle = vehicleMapper.selectById(booking.getVehicleId());
        if (vehicle == null) {
            throw BusinessException.notFound("车辆不存在");
        }
        if (!"confirmed".equals(vehicle.getStatus()) && !"reserved".equals(vehicle.getStatus())) {
            throw BusinessException.businessFail("车辆状态异常，无法取车");
        }

        LocalDateTime now = LocalDateTime.now();
        booking.setActualPickupTime(now);
        booking.setPickupMileage(dto.getPickupMileage());
        // 计划还车时间 = 取车时间 + 计划天数
        booking.setPlannedReturnTime(now.plusDays(booking.getPlannedDays()));
        booking.setStatus("picked_up");
        bookingMapper.updateById(booking);

        vehicle.setStatus("rented");
        vehicle.setCurrentMileage(dto.getPickupMileage());
        vehicleMapper.updateById(vehicle);

        return buildVO(booking);
    }

    @Override
    @Transactional
    public BookingVO returnVehicle(ReturnDTO dto) {
        Booking booking = bookingMapper.selectById(dto.getBookingId());
        if (booking == null) {
            throw BusinessException.notFound("预订不存在");
        }
        if (!"picked_up".equals(booking.getStatus()) && !"overdue".equals(booking.getStatus())) {
            throw BusinessException.businessFail("当前预订状态不允许还车，状态：" +
                    STATUS_NAMES.getOrDefault(booking.getStatus(), booking.getStatus()));
        }

        // 校验还车里程
        if (booking.getPickupMileage() == null) {
            throw BusinessException.businessFail("取车里程记录异常");
        }
        if (dto.getReturnMileage() < booking.getPickupMileage()) {
            throw BusinessException.paramError("还车里程不能低于取车里程(" + booking.getPickupMileage() + "km)");
        }

        Vehicle vehicle = vehicleMapper.selectById(booking.getVehicleId());
        if (vehicle == null) {
            throw BusinessException.notFound("车辆不存在");
        }

        VehicleConfig config = configMapper.selectOne(new LambdaQueryWrapper<VehicleConfig>()
                .eq(VehicleConfig::getType, vehicle.getType()));
        if (config == null) {
            throw BusinessException.businessFail("车辆配置不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        booking.setActualReturnTime(now);
        booking.setReturnMileage(dto.getReturnMileage());

        // ===== 费用计算 =====
        boolean isOverdue = now.isAfter(booking.getPlannedReturnTime());
        int overtimeMinutes = 0;
        int overtimeHours = 0;
        BigDecimal overtimeFee = BigDecimal.ZERO;

        if (isOverdue) {
            overtimeMinutes = (int) ChronoUnit.MINUTES.between(booking.getPlannedReturnTime(), now);
            if (overtimeMinutes < 0) overtimeMinutes = 0;
            // ⭐ 向上取整：超5分钟算1小时，超61分钟算2小时
            overtimeHours = (int) Math.ceil(overtimeMinutes / 60.0);
            overtimeFee = config.getOvertimeRate().multiply(BigDecimal.valueOf(overtimeHours));
        }

        // 超里程费：总免费额 = 天数 × 每天免费里程（叠加，不是每天单独计算）
        int actualMileage = dto.getReturnMileage() - booking.getPickupMileage();
        int totalFreeMileage = booking.getPlannedDays() * config.getFreeMileage();
        int excessMileage = Math.max(0, actualMileage - totalFreeMileage);
        BigDecimal excessMileageFee = config.getMileageRate().multiply(BigDecimal.valueOf(excessMileage));

        // 总费用 = 预计租金 + 超时费 + 超里程费
        BigDecimal totalFee = booking.getEstimatedRent().add(overtimeFee).add(excessMileageFee);

        // 写入费用快照
        booking.setOvertimeMinutes(overtimeMinutes);
        booking.setOvertimeFee(overtimeFee);
        booking.setExcessMileageFee(excessMileageFee);
        booking.setTotalFee(totalFee);
        booking.setStatus("returned");
        bookingMapper.updateById(booking);

        // 更新车辆
        vehicle.setCurrentMileage(dto.getReturnMileage());
        vehicle.setStatus("idle");
        vehicleMapper.updateById(vehicle);

        // ⭐ 信用分扣减：超时固定扣5分，信用分最低为0
        if (isOverdue) {
            User user = userMapper.selectById(booking.getUserId());
            if (user != null) {
                int currentScore = user.getCreditScore() != null ? user.getCreditScore() : 100;
                int newScore = Math.max(0, currentScore - 5);
                user.setCreditScore(newScore);
                userMapper.updateById(user);
            }
        }

        return buildVO(booking);
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
