package com.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleConfigVO {

    private Long id;

    private String name;

    private String type;

    private BigDecimal dailyRate;

    private BigDecimal overtimeRate;

    private Integer freeMileage;

    private BigDecimal mileageRate;
}
