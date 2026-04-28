package com.carrental.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @Size(min = 2, max = 50, message = "真实姓名长度为2-50个字符")
    private String realName;

    @Size(max = 18, message = "驾照号最多18位")
    private String licenseNo;

    @Size(max = 20, message = "手机号最多20位")
    private String phone;

    private String role;

    private Integer status;

    private Integer creditScore;
}
