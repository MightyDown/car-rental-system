package com.carrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("booking")
public class Booking {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String bookingNo;

    private Long userId;

    private Long vehicleId;

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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
