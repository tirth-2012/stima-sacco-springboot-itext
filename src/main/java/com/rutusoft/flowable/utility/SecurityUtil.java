package com.rutusoft.flowable.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final IdentityService identityService;

    /**
     * Returns currently logged-in user id
     */
    public String getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName(); // BEST PRACTICE
    }

    public List<String> getCurrentUserGroups() {
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        Collection<? extends GrantedAuthority> authorities =
//                authentication.getAuthorities();
//
//        List<String> groups = authorities.stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//
//        return groups;

        List<String> groups = new ArrayList<>();
        List<Group> userGroups = identityService.createGroupQuery().groupMember(getCurrentUserId()).list();
        for(Group group : userGroups) {
            groups.add(group.getId());
        }

        return groups;
    }

    /**
     * Returns full name of logged-in user
     */
    public String getCurrentUserFullName() {

        String userId = getCurrentUserId();
        if (userId == null) {
            log.warn("No authenticated user found");
            return "";
        }

        User user = identityService
                .createUserQuery()
                .userId(userId)
                .singleResult();

        if (user == null) {
            log.warn("User not found in Flowable IDM: {}", userId);
            return "";
        }

        return String.format("%s %s",
                user.getFirstName(),
                user.getLastName());
    }

    public String getFullNameByUserId(String userId) {
        User user = identityService.createUserQuery().userId(userId).singleResult();
        if (user == null) {
            log.warn("User not found in Flowable IDM: {}", userId);
            return "";
        }

        return String.format("%s %s", user.getFirstName(), user.getLastName());
    }

    public String getEmailId(String userId) {
        User user = identityService
                .createUserQuery()
                .userId(userId)
                .singleResult();

        if (user == null) {
            log.warn("User not found in Flowable IDM: {}", userId);
            return "";
        }
        return user.getEmail();
    }


}
