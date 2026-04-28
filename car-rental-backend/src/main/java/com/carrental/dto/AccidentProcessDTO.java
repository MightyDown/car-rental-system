package com.carrental.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AccidentProcessDTO {

    @NotNull(message = "扣分值不能为空")
    @Min(value = 0, message = "扣分值不能小于0")
    private Integer deductionPoints;

    @NotNull(message = "预计完成日期不能为空")
    private LocalDate expectedCompletionDate;
}
