package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.CreateUserDto;
import com.rutusoft.flowable.dto.UpdateUserProfileDto;
import com.rutusoft.flowable.dto.UserDto;
import com.rutusoft.flowable.dto.UserLoginRequestDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface UserManagementService {

    Map<String, Object> login(UserLoginRequestDto userLoginRequestDto);

    String logout(HttpServletRequest request, HttpServletResponse response);

    String createUser(CreateUserDto userDto);

    List<UserDto> listUsers(int from, int to);

    Map<String, Object> getCurrentUserProfile();

    String updateCurrentUserProfile(UpdateUserProfileDto dto);

    String resetPassword(String oldPassword, String newPassword);

    String forgotPassword(String usernameOrEmail);

    String resetPasswordWithToken(String token, String newPassword);

}
