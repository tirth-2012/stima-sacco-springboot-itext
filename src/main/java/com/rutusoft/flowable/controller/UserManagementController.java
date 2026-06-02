package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.CreateUserDto;
import com.rutusoft.flowable.dto.UpdateUserProfileDto;
import com.rutusoft.flowable.dto.UserDto;
import com.rutusoft.flowable.dto.UserLoginRequestDto;
import com.rutusoft.flowable.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserManagementController {
    @Autowired
    private UserManagementService userManagementService;

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        return new ResponseEntity<>(userManagementService.login(userLoginRequestDto),
                HttpStatus.OK);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        return new ResponseEntity<>(userManagementService.logout(request, response), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<String> createUser(CreateUserDto userDto) {
        return new ResponseEntity<>(userManagementService.createUser(userDto), HttpStatus.OK);
    }

    @GetMapping("/current-user-profile")
    public ResponseEntity<?> getCurrentUserProfile() {
        return ResponseEntity.ok(userManagementService.getCurrentUserProfile());
    }

    @PutMapping("/current-user-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserProfileDto dto) {
        return ResponseEntity.ok(userManagementService.updateCurrentUserProfile(dto));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String oldPassword,
                                           @RequestParam String newPassword) {
        return ResponseEntity.ok(userManagementService.resetPassword(oldPassword, newPassword));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String usernameOrEmail) {
        return ResponseEntity.ok(userManagementService.forgotPassword(usernameOrEmail));
    }

    @PostMapping("/reset-password-with-token")
    public ResponseEntity<?> resetPasswordWithToken(@RequestParam String token,
                                                    @RequestParam String newPassword) {
        return ResponseEntity.ok(
                userManagementService.resetPasswordWithToken(token, newPassword)
        );
    }
}
