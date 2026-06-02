package com.rutusoft.flowable.service.impl;

import com.auth0.jwt.JWT;
import com.rutusoft.flowable.dto.CreateUserDto;
import com.rutusoft.flowable.dto.UpdateUserProfileDto;
import com.rutusoft.flowable.dto.UserDto;
import com.rutusoft.flowable.dto.UserLoginRequestDto;
import com.rutusoft.flowable.entity.UserToken;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.repository.UserTokenRepository;
import com.rutusoft.flowable.service.UserManagementService;
import com.rutusoft.flowable.utility.MailNotificationUtil;
import com.rutusoft.flowable.utility.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.IdentityService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import org.thymeleaf.context.Context;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.util.*;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.rutusoft.flowable.security.SecurityConstants.EXPIRATION_TIME;
import static com.rutusoft.flowable.security.SecurityConstants.SECRET;

@Service
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {
    @Autowired
    private IdentityService identityService;

    @Autowired
    private AuthenticationProvider customAuthenticationProvider;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private ProcessEngineConfigurationImpl processEngineConfiguration;

    @Autowired
    private MailNotificationUtil mailNotificationUtil;

    @Override
    public Map<String, Object> login(UserLoginRequestDto userLoginRequest) {
        log.info("username : {}, password : {}", userLoginRequest.getUserId(),
                userLoginRequest.getPassword());
        Map<String, Object> responseMap = new HashMap<>();
        String token = null;
        try {
            authenticate(userLoginRequest.getUserId(), userLoginRequest.getPassword());

            User user = identityService.createUserQuery().userId(userLoginRequest.getUserId()).singleResult();

            if (user == null) {
                throw new UsernameNotFoundException(userLoginRequest.getUserId());
            }

            List<Group> groups = identityService.createGroupQuery().groupMember(userLoginRequest.getUserId())
                    .list();
            responseMap.put("groups", groups);

            token = JWT.create().withSubject(userLoginRequest.getUserId())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(HMAC512(SECRET.getBytes()));

            String branchCode = identityService.getUserInfo(userLoginRequest.getUserId(), "branchCode");
            String regionCode = identityService.getUserInfo(userLoginRequest.getUserId(), "regionCode");

            responseMap.put("token", token);
            responseMap.put("firstname", user.getFirstName());
            responseMap.put("lastname", user.getLastName());
            responseMap.put("userId", user.getId());
            responseMap.put("groups", groups);
            responseMap.put("branchCode", branchCode);
            responseMap.put("regionCode", regionCode);

        } catch (Exception e) {
            log.error("Exception occurred while authenticating user : ", e);
            throw e;
        }

        return responseMap;
    }

    @Override
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        //dentityService.clearAuthentication();

        log.info("Invalidating session");
        return "User logged out successfully";
    }

    @Override
    public String createUser(CreateUserDto userDto) {
        log.info("Creating new user : {}", userDto);

        if (userDto.getUserId() == null) {
            throw new ValidationException("Userid is mandatory");
        }

        List<User> users = identityService.createUserQuery().userId(userDto.getUserId()).list();
        User user = null;
        if (!users.isEmpty()) {
            user = users.get(0);
        } else {
            user = identityService.newUser(userDto.getUserId());
        }
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setDisplayName(userDto.getFirstName() + " " + userDto.getLastName());
        identityService.saveUser(user);

        for (String groupId : userDto.getGroups()) {
            List<User> usersWithGroup = identityService.createUserQuery().userId(userDto.getUserId()).memberOfGroup(groupId).list();
            if(usersWithGroup.isEmpty()) {
                identityService.createMembership(userDto.getUserId(), groupId);
            }
        }

        identityService.setUserInfo(userDto.getUserId(), "branchCode", userDto.getBranchCode());
        identityService.setUserInfo(userDto.getUserId(), "regionCode", userDto.getRegionCode());
        return "User created successfully";
    }

    @Override
    public List<UserDto> listUsers(int from, int to) {
        List<UserDto> userDtos = new ArrayList<>();
        List<User> users = identityService.createUserQuery().listPage(from, to);
        for(User user : users) {
            UserDto userDto = new UserDto();
            userDto.setUserId(user.getId());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setDisplayName(user.getDisplayName());
            userDto.setEmail(user.getEmail());

            List<Group> groups = identityService.createGroupQuery().groupMember(user.getId()).list();
            userDto.setGroups(groups);

            userDtos.add(userDto);
        }
        return userDtos;
    }

    private void authenticate(String username, String password) {
        try {
            customAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }

    // Get Current User Profile
    @Override
    public Map<String, Object> getCurrentUserProfile() {

        String userId = securityUtil.getCurrentUserId();

        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }

        User user = identityService.createUserQuery()
                .userId(userId)
                .singleResult();

        List<Group> groups = identityService
                .createGroupQuery()
                .groupMember(userId)
                .list();

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("email", user.getEmail());
        response.put("mobileNo", identityService.getUserInfo(userId, "mobileNo"));
        response.put("groups", groups);

        return response;
    }

    // Update User Profile
    @Override
    public String updateCurrentUserProfile(UpdateUserProfileDto dto) {

        String userId = securityUtil.getCurrentUserId();

        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }

        User user = identityService.createUserQuery()
                .userId(userId)
                .singleResult();

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // ✅ Only allowed fields
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getDisplayName() != null) {
            user.setDisplayName(dto.getDisplayName());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        identityService.saveUser(user);

        if (dto.getMobileNo() != null) {
            identityService.setUserInfo(userId, "mobileNo", dto.getMobileNo());
        }

        return "Profile updated successfully";
    }

    // helper for password validation
    private void validatePasswordStrength(String password) {

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        if (!password.matches(".*[@$!%*?&].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character (@$!%*?&)");
        }
    }

    // reset password
    @Transactional
    @Override
    public String resetPassword(String oldPassword, String newPassword) {

        String userId = securityUtil.getCurrentUserId();

        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }

        validatePasswordStrength(newPassword);

        // verify old password
        authenticate(userId, oldPassword);

        User user = identityService.createUserQuery()
                .userId(userId)
                .singleResult();

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setPassword(newPassword);

        identityService.updateUserPassword(user); // ✅ correct

        return "Password updated successfully";
    }

    // helper method to save user token
    private void saveUserToken(String userId, String token, Date expiry) {

        userTokenRepository.deleteByUserId(userId);

        UserToken userToken = new UserToken();
        userToken.setToken(token);
        userToken.setUserId(userId);
        userToken.setExpiryTime(expiry);

        userTokenRepository.save(userToken);
    }

    // opt / token generation
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // 6-digit
        return String.valueOf(otp);
    }

    // reset password with token
    @Transactional
    @Override
    public String resetPasswordWithToken(String token, String newPassword) {

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("OTP is required");
        }

        validatePasswordStrength(newPassword);

        UserToken userToken = userTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        // Check expiry
        if (userToken.getExpiryTime().before(new Date())) {
            userTokenRepository.delete(userToken);
            throw new RuntimeException("OTP has expired");
        }

        String userId = userToken.getUserId();

        User user = identityService.createUserQuery()
                .userId(userId)
                .singleResult();

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // FIX: correct Flowable method
        user.setPassword(newPassword);
        identityService.updateUserPassword(user);

        // delete OTP after success
        userTokenRepository.delete(userToken);

        return "Password reset successful";
    }

    //forgot password
    @Transactional
    @Override
    public String forgotPassword(String usernameOrEmail) {

        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Username or email is required");
        }

        // Find user
        User user = identityService.createUserQuery()
                .userId(usernameOrEmail)
                .singleResult();

        if (user == null) {
            user = identityService.createUserQuery()
                    .userEmail(usernameOrEmail)
                    .singleResult();
        }

        // Don't reveal user existence
        if (user == null) {
            log.warn("Forgot password attempt for non-existing user: {}", usernameOrEmail);
            return "If the account exists, an OTP has been sent";
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            log.warn("User {} has no email configured", user.getId());
            return "If the account exists, an OTP has been sent";
        }

        // Generate OTP
        String otp = generateOtp();

        // Expiry (5 min)
        Date expiry = new Date(System.currentTimeMillis() + (5 * 60 * 1000));

        // One active OTP per user
        userTokenRepository.deleteByUserId(user.getId());

        saveUserToken(user.getId(), otp, expiry);

        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("userName", user.getFirstName());
        context.setVariable("otp", otp);
        context.setVariable("expiryMinutes", 5);

        // Send email using template
        mailNotificationUtil.sendEmail(
                user.getEmail(),
                "Password Reset OTP",
                "email/forgot-password-otp",
                context
        );

        log.info("OTP sent to user {}", user.getId());

        return "If the account exists, an OTP has been sent";
    }
}
