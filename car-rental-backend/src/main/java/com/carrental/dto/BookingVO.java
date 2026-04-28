package com.carrental.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingVO {

    private Long id;

    private String bookingNo;

    private Long userId;

    private String userName;

    private String userRealName;

    private Long vehicleId;

    private String vehiclePlateNo;

    private String vehicleModel;

    private String vehicleType;

    private String vehicleTypeName;

    private LocalDateTime plannedPickupTime;

    private LocalDateTime plannedReturnTime;

    private Integer plannedDays;

    private BigDecimal estimatedRent;

    private LocalDateTime actualPickupTime;

    private Integer pickupMileage;

    private LocalDateTime actualReturnTime;

    private Integer returnMileage;

    private Integer overtimeMinutes;

    private BigDecimal overtimeFee;

    private BigDecimal excessMileageFee;

    private BigDecimal totalFee;

    private String status;

    private String statusName;

    private LocalDateTime createTime;
}
