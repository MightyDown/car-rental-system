package com.carrental.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingSaveDTO {

    @NotNull(message = "车辆ID不能为空")
    private Long vehicleId;

    @NotNull(message = "计划取车时间不能为空")
    @Future(message = "计划取车时间必须是将来的时间")
    private LocalDateTime plannedPickupTime;

    @NotNull(message = "计划还车时间不能为空")
    private LocalDateTime plannedReturnTime;
}
