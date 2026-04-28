package com.carrental.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class AccidentVO {

    private Long id;

    private Long bookingId;

    private String bookingNo;

    private Long vehicleId;

    private String vehiclePlateNo;

    private String vehicleModel;

    private Long userId;

    private String userName;

    private String userRealName;

    private String description;

    private Integer deductionPoints;

    private LocalDate expectedCompletionDate;

    private LocalDate actualCompletionDate;

    private String status;

    private String statusName;

    private LocalDateTime createTime;
}
