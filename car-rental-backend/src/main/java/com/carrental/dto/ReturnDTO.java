package com.carrental.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReturnDTO {

    @NotNull(message = "预订ID不能为空")
    private Long bookingId;

    @NotNull(message = "还车里程不能为空")
    @Min(value = 0, message = "里程不能小于0")
    private Integer returnMileage;
}
