package com.carrental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.common.PageResult;
import com.carrental.dto.*;
import com.carrental.entity.Accident;
import com.carrental.entity.Booking;
import com.carrental.entity.User;
import com.carrental.entity.Vehicle;
import com.carrental.exception.BusinessException;
import com.carrental.mapper.AccidentMapper;
import com.carrental.mapper.BookingMapper;
import com.carrental.mapper.UserMapper;
import com.carrental.mapper.VehicleMapper;
import com.carrental.service.AccidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccidentServiceImpl implements AccidentService {

    private final AccidentMapper accidentMapper;
    private final BookingMapper bookingMapper;
    private final VehicleMapper vehicleMapper;
    private final UserMapper userMapper;

    private static final Map<String, String> STATUS_NAMES = new LinkedHashMap<>();

    static {
        STATUS_NAMES.put("pending", "待处理");
        STATUS_NAMES.put("in_progress", "处理中");
        STATUS_NAMES.put("completed", "已完成");
    }

    @Override
    public PageResult<AccidentVO> listAccidents(Integer page, Integer size, String status) {
        LambdaQueryWrapper<Accident> wrapper = new LambdaQueryWrapper<Accident>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Accident::getStatus, status);
        }
        wrapper.orderByDesc(Accident::getCreateTime);

        IPage<Accident> iPage = accidentMapper.selectPage(new Page<>(page, size), wrapper);
        return PageResult.from(iPage.convert(this::buildVO));
    }

    @Override
    public AccidentVO getAccidentById(Long id) {
        Accident accident = accidentMapper.selectById(id);
        if (accident == null) {
            throw BusinessException.notFound("事故记录不存在");
        }
        return buildVO(accident);
    }

    @Override
    @Transactional
    public AccidentVO createAccident(AccidentSaveDTO dto) {
        Booking booking = bookingMapper.selectById(dto.getBookingId());
        if (booking == null) {
            throw BusinessException.notFound("预订不存在");
        }

        Vehicle vehicle = vehicleMapper.selectById(dto.getVehicleId());
        if (vehicle == null) {
            throw BusinessException.notFound("车辆不存在");
        }

        if ("maintenance".equals(vehicle.getStatus())) {
            throw BusinessException.businessFail("车辆已在维修中");
        }

        Accident accident = new Accident();
        accident.setBookingId(dto.getBookingId());
        accident.setVehicleId(dto.getVehicleId());
        accident.setDescription(dto.getDescription());
        accident.setStatus("pending");
        accidentMapper.insert(accident);

        vehicle.setStatus("maintenance");
        vehicleMapper.updateById(vehicle);

        return buildVO(accident);
    }

    @Override
    @Transactional
    public void processAccident(Long id, AccidentProcessDTO dto) {
        Accident accident = accidentMapper.selectById(id);
        if (accident == null) {
            throw BusinessException.notFound("事故记录不存在");
        }
        if (!"pending".equals(accident.getStatus())) {
            throw BusinessException.businessFail("当前事故状态不允许处理");
        }

        accident.setDeductionPoints(dto.getDeductionPoints());
        accident.setExpectedCompletionDate(dto.getExpectedCompletionDate());
        accident.setStatus("in_progress");
        accidentMapper.updateById(accident);

        // 扣减用户信用分，最低为0
        Booking booking = bookingMapper.selectById(accident.getBookingId());
        if (booking != null) {
            User user = userMapper.selectById(booking.getUserId());
            if (user != null) {
                int currentScore = user.getCreditScore() != null ? user.getCreditScore() : 100;
                int newScore = Math.max(0, currentScore - dto.getDeductionPoints());
                user.setCreditScore(newScore);
                userMapper.updateById(user);
            }
        }
    }

    @Override
    @Transactional
    public void completeAccident(Long id) {
        Accident accident = accidentMapper.selectById(id);
        if (accident == null) {
            throw BusinessException.notFound("事故记录不存在");
        }
        if (!"in_progress".equals(accident.getStatus())) {
            throw BusinessException.businessFail("当前事故状态不允许完成维修");
        }

        accident.setActualCompletionDate(java.time.LocalDate.now());
        accident.setStatus("completed");
        accidentMapper.updateById(accident);

        // 检查该车辆是否还有其他未完成的事故
        Long pendingCount = accidentMapper.selectCount(new LambdaQueryWrapper<Accident>()
                .eq(Accident::getVehicleId, accident.getVehicleId())
                .ne(Accident::getId, id)
                .in(Accident::getStatus, "pending", "in_progress"));

        if (pendingCount == 0) {
            Vehicle vehicle = vehicleMapper.selectById(accident.getVehicleId());
            if (vehicle != null && "maintenance".equals(vehicle.getStatus())) {
                vehicle.setStatus("idle");
                vehicleMapper.updateById(vehicle);
            }
        }
    }

    private AccidentVO buildVO(Accident a) {
        Booking booking = a.getBookingId() != null ? bookingMapper.selectById(a.getBookingId()) : null;
        Vehicle vehicle = a.getVehicleId() != null ? vehicleMapper.selectById(a.getVehicleId()) : null;
        User user = booking != null ? userMapper.selectById(booking.getUserId()) : null;

        return AccidentVO.builder()
                .id(a.getId())
                .bookingId(a.getBookingId())
                .bookingNo(booking != null ? booking.getBookingNo() : null)
                .vehicleId(a.getVehicleId())
                .vehiclePlateNo(vehicle != null ? vehicle.getPlateNo() : null)
                .vehicleModel(vehicle != null ? vehicle.getModel() : null)
                .userId(booking != null ? booking.getUserId() : null)
                .userName(user != null ? user.getUsername() : null)
                .userRealName(user != null ? user.getRealName() : null)
                .description(a.getDescription())
                .deductionPoints(a.getDeductionPoints())
                .expectedCompletionDate(a.getExpectedCompletionDate())
                .actualCompletionDate(a.getActualCompletionDate())
                .status(a.getStatus())
                .statusName(STATUS_NAMES.getOrDefault(a.getStatus(), a.getStatus()))
                .createTime(a.getCreateTime())
                .build();
    }
}
