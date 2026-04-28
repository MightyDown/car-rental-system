package com.carrental.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserVO {

    private Long id;

    private String username;

    private String realName;

    private String licenseNo;

    private String phone;

    private String role;

    private Integer status;

    private Integer creditScore;

    private LocalDateTime createTime;
}
