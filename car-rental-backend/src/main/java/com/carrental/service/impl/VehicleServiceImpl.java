package com.carrental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carrental.common.PageResult;
import com.carrental.dto.VehicleConfigVO;
import com.carrental.dto.VehicleSaveDTO;
import com.carrental.dto.VehicleVO;
import com.carrental.entity.Vehicle;
import com.carrental.entity.VehicleConfig;
import com.carrental.exception.BusinessException;
import com.carrental.mapper.VehicleConfigMapper;
import com.carrental.mapper.VehicleMapper;
import com.carrental.service.VehicleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleMapper vehicleMapper;
    private final VehicleConfigMapper configMapper;

    private static final Map<String, String> STATUS_NAMES = new HashMap<>();
    private static final Map<String, String> TYPE_NAMES = new HashMap<>();

    static {
        STATUS_NAMES.put("idle", "空闲");
        STATUS_NAMES.put("reserved", "已预约");
        STATUS_NAMES.put("confirmed", "已预留");
        STATUS_NAMES.put("rented", "已租用");
        STATUS_NAMES.put("maintenance", "维修中");

        TYPE_NAMES.put("economy", "经济型");
        TYPE_NAMES.put("luxury", "豪华型");
    }

    @PostConstruct
    public void initConfigs() {
        createConfigIfAbsent("经济型", "economy",
                new BigDecimal("200"), new BigDecimal("20"), 200, new BigDecimal("1"));
        createConfigIfAbsent("豪华型", "luxury",
                new BigDecimal("600"), new BigDecimal("50"), 150, new BigDecimal("2"));
    }

    private void createConfigIfAbsent(String name, String type, BigDecimal dailyRate,
                                       BigDecimal overtimeRate, Integer freeMileage, BigDecimal mileageRate) {
        Long count = configMapper.selectCount(new LambdaQueryWrapper<VehicleConfig>()
                .eq(VehicleConfig::getType, type));
        if (count == 0) {
            VehicleConfig config = new VehicleConfig();
            config.setName(name);
            config.setType(type);
            config.setDailyRate(dailyRate);
            config.setOvertimeRate(overtimeRate);
            config.setFreeMileage(freeMileage);
            config.setMileageRate(mileageRate);
            configMapper.insert(config);
        }
    }

    @Override
    public PageResult<VehicleVO> listVehicles(Integer page, Integer size, String keyword, String type, String status) {
        LambdaQueryWrapper<Vehicle> wrapper = new LambdaQueryWrapper<Vehicle>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(Vehicle::getPlateNo, keyword)
                    .or()
                    .like(Vehicle::getModel, keyword));
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(Vehicle::getType, type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Vehicle::getStatus, status);
        }
        wrapper.orderByDesc(Vehicle::getCreateTime);

        IPage<Vehicle> iPage = vehicleMapper.selectPage(new Page<>(page, size), wrapper);

        Map<String, VehicleConfig> configMap = loadConfigMap();

        return PageResult.from(iPage.convert(v -> buildVO(v, configMap)));
    }

    @Override
    public VehicleVO getVehicleById(Long id) {
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw BusinessException.notFound("车辆不存在");
        }
        return buildVO(vehicle, loadConfigMap());
    }

    @Override
    public void createVehicle(VehicleSaveDTO dto) {
        if (!TYPE_NAMES.containsKey(dto.getType())) {
            throw BusinessException.paramError("车辆类型必须为 economy 或 luxury");
        }
        Long count = vehicleMapper.selectCount(new LambdaQueryWrapper<Vehicle>()
                .eq(Vehicle::getPlateNo, dto.getPlateNo()));
        if (count > 0) {
            throw BusinessException.conflict("车牌号已存在");
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNo(dto.getPlateNo());
        vehicle.setModel(dto.getModel());
        vehicle.setType(dto.getType());
        vehicle.setCurrentMileage(dto.getCurrentMileage());
        vehicle.setImage(dto.getImage());
        vehicle.setStatus("idle");
        vehicleMapper.insert(vehicle);
    }

    @Override
    public void updateVehicle(Long id, VehicleSaveDTO dto) {
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw BusinessException.notFound("车辆不存在");
        }
        if (!vehicle.getPlateNo().equals(dto.getPlateNo())) {
            Long count = vehicleMapper.selectCount(new LambdaQueryWrapper<Vehicle>()
                    .eq(Vehicle::getPlateNo, dto.getPlateNo()));
            if (count > 0) {
                throw BusinessException.conflict("车牌号已存在");
            }
        }
        if (!TYPE_NAMES.containsKey(dto.getType())) {
            throw BusinessException.paramError("车辆类型必须为 economy 或 luxury");
        }
        vehicle.setPlateNo(dto.getPlateNo());
        vehicle.setModel(dto.getModel());
        vehicle.setType(dto.getType());
        vehicle.setCurrentMileage(dto.getCurrentMileage());
        if (dto.getImage() != null) {
            vehicle.setImage(dto.getImage());
        }
        vehicleMapper.updateById(vehicle);
    }

    @Override
    public void updateStatus(Long id, String status) {
        if (!STATUS_NAMES.containsKey(status)) {
            throw BusinessException.paramError("无效的车辆状态");
        }
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw BusinessException.notFound("车辆不存在");
        }
        vehicle.setStatus(status);
        vehicleMapper.updateById(vehicle);
    }

    @Override
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw BusinessException.notFound("车辆不存在");
        }
        if (!"idle".equals(vehicle.getStatus()) && !"maintenance".equals(vehicle.getStatus())) {
            throw BusinessException.businessFail("车辆正在使用中，无法删除");
        }
        vehicleMapper.deleteById(id);
    }

    @Override
    public List<VehicleConfigVO> listConfigs() {
        return configMapper.selectList(null).stream()
                .map(c -> VehicleConfigVO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .type(c.getType())
                        .dailyRate(c.getDailyRate())
                        .overtimeRate(c.getOvertimeRate())
                        .freeMileage(c.getFreeMileage())
                        .mileageRate(c.getMileageRate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void updateConfig(Long id, VehicleConfigVO vo) {
        VehicleConfig config = configMapper.selectById(id);
        if (config == null) {
            throw BusinessException.notFound("配置不存在");
        }
        if (vo.getDailyRate() != null) config.setDailyRate(vo.getDailyRate());
        if (vo.getOvertimeRate() != null) config.setOvertimeRate(vo.getOvertimeRate());
        if (vo.getFreeMileage() != null) config.setFreeMileage(vo.getFreeMileage());
        if (vo.getMileageRate() != null) config.setMileageRate(vo.getMileageRate());
        configMapper.updateById(config);
    }

    private Map<String, VehicleConfig> loadConfigMap() {
        return configMapper.selectList(null).stream()
                .collect(Collectors.toMap(VehicleConfig::getType, c -> c));
    }

    private VehicleVO buildVO(Vehicle v, Map<String, VehicleConfig> configMap) {
        VehicleConfig cfg = configMap.get(v.getType());
        return VehicleVO.builder()
                .id(v.getId())
                .plateNo(v.getPlateNo())
                .model(v.getModel())
                .type(v.getType())
                .typeName(TYPE_NAMES.getOrDefault(v.getType(), v.getType()))
                .status(v.getStatus())
                .statusName(STATUS_NAMES.getOrDefault(v.getStatus(), v.getStatus()))
                .currentMileage(v.getCurrentMileage())
                .image(v.getImage())
                .dailyRate(cfg != null ? cfg.getDailyRate() : null)
                .overtimeRate(cfg != null ? cfg.getOvertimeRate() : null)
                .freeMileage(cfg != null ? cfg.getFreeMileage() : null)
                .mileageRate(cfg != null ? cfg.getMileageRate() : null)
                .createTime(v.getCreateTime())
                .build();
    }
}
