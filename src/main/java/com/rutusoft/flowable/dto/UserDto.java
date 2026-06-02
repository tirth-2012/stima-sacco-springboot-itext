package com.rutusoft.flowable.dto;

import lombok.Data;
import org.flowable.idm.api.Group;
import java.util.List;

@Data
public class UserDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String mobileNo;
    private List<Group> groups;
}
