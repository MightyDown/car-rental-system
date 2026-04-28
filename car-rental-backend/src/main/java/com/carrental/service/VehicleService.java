package com.carrental.service;

import com.carrental.common.PageResult;
import com.carrental.dto.VehicleConfigVO;
import com.carrental.dto.VehicleSaveDTO;
import com.carrental.dto.VehicleVO;

import java.util.List;

public interface VehicleService {

    PageResult<VehicleVO> listVehicles(Integer page, Integer size, String keyword, String type, String status);

    VehicleVO getVehicleById(Long id);

    void createVehicle(VehicleSaveDTO dto);

    void updateVehicle(Long id, VehicleSaveDTO dto);

    void updateStatus(Long id, String status);

    void deleteVehicle(Long id);

    List<VehicleConfigVO> listConfigs();

    void updateConfig(Long id, VehicleConfigVO vo);
}
