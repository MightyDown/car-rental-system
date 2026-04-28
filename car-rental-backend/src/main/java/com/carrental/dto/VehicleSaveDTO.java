package com.carrental.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleSaveDTO {

    @NotBlank(message = "车牌号不能为空")
    private String plateNo;

    @NotBlank(message = "车型名称不能为空")
    private String model;

    @NotBlank(message = "车辆类型不能为空")
    private String type;

    @NotNull(message = "当前里程不能为空")
    @Min(value = 0, message = "里程不能小于0")
    private Integer currentMileage;

    private String image;
}
