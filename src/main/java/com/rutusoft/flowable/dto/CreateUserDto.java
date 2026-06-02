package com.rutusoft.flowable.dto;

import lombok.Data;
import org.flowable.idm.api.Group;

import java.util.List;
import java.util.Set;

@Data
public class CreateUserDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNo;
    private String branchCode;
    private String regionCode;
    private String password;
    private List<String> groups;
}
