package com.carrental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("accident")
public class Accident {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookingId;

    private Long vehicleId;

    private String description;

    private Integer deductionPoints;

    private LocalDate expectedCompletionDate;

    private LocalDate actualCompletionDate;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
