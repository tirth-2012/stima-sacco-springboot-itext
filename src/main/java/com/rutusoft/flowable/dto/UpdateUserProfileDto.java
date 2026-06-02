package com.rutusoft.flowable.dto;

import lombok.Data;

@Data
public class UpdateUserProfileDto {

    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String mobileNo;
}