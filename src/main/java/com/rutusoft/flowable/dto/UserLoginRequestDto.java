package com.rutusoft.flowable.dto;

import lombok.Data;

@Data
public class UserLoginRequestDto {
    private String userId;
    private String password;
}
