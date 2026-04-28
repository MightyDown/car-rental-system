package com.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccidentSaveDTO {

    @NotNull(message = "预订ID不能为空")
    private Long bookingId;

    @NotNull(message = "车辆ID不能为空")
    private Long vehicleId;

    @NotBlank(message = "事故描述不能为空")
    private String description;
}
