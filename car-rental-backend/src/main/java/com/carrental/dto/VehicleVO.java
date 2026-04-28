package com.carrental.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class VehicleVO {

    private Long id;

    private String plateNo;

    private String model;

    private String type;

    private String typeName;

    private String status;

    private String statusName;

    private Integer currentMileage;

    private String image;

    private BigDecimal dailyRate;

    private BigDecimal overtimeRate;

    private Integer freeMileage;

    private BigDecimal mileageRate;

    private LocalDateTime createTime;
}
