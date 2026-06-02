package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.GroupDto;
import com.rutusoft.flowable.exception.ItemNotFoundException;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.service.GroupManagementService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.GroupQuery;
import org.flowable.idm.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GroupManagementServiceImpl implements GroupManagementService {
    @Autowired
    private IdentityService identityService;
    @Override
    public String createNewGroup(GroupDto groupDto) {
        Group group = identityService.createGroupQuery()
                .groupId(groupDto.getGroupId())
                .singleResult();

        log.info("Searched group: {}", group);

        if (group == null) {
            log.info("Adding new group with ID: {}", groupDto.getGroupId());

            Group createdGroup = identityService.newGroup(groupDto.getGroupId()); // ✅ ID
            createdGroup.setName(groupDto.getName());                             // ✅ Name
            createdGroup.setType("assignment");

            identityService.saveGroup(createdGroup);
            log.info("Group {} added successfully", groupDto.getGroupId());
            return "Group "+groupDto.getGroupId()+" is already created";
        }

        return "Group "+groupDto.getGroupId()+" is already created";
    }

    @Override
    public List<GroupDto> listGroups() {
        List<GroupDto> groupDtos = new ArrayList<>();
        List<Group> groups = identityService.createGroupQuery().list();
        for(Group group : groups) {
            GroupDto groupDto = new GroupDto();
            groupDto.setGroupId(group.getId());
            groupDto.setName(group.getName());
            groupDtos.add(groupDto);
        }
        return groupDtos;
    }

    @Override
    public GroupDto getGroupByGroupId(String groupId) {
        Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
        if(group == null) {
            throw new ItemNotFoundException("Group with id "+groupId+" does not exist");
        }

        GroupDto groupDto = new GroupDto();
        groupDto.setGroupId(group.getId());
        groupDto.setName(group.getName());
        return groupDto;

    }

    @Override
    public String addMemberToGroup(String groupId, String userId) {
        Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
        if(group == null) {
            throw new ItemNotFoundException("Group with id "+groupId+" does not exist");
        }

        User user = identityService.createUserQuery().userId(userId).singleResult();
        if(user == null) {
            throw new ItemNotFoundException("User with user id "+userId+" does not exist");
        }

        User groupMember = identityService.createUserQuery().memberOfGroup(groupId).singleResult();
        if(groupMember != null) {
            throw new ValidationException("User "+userId+" is already member of group "+groupId);

        }

        identityService.createMembership(userId, groupId);
        return "User "+userId+" added to group "+groupId;
    }

    @Override
    public String deleteMemberFromGroup(String groupId, String userId) {
        Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
        if(group == null) {
            throw new ItemNotFoundException("Group with id "+groupId+" does not exist");
        }

        User user = identityService.createUserQuery().userId(userId).singleResult();
        if(user == null) {
            throw new ItemNotFoundException("User with user id "+userId+" does not exist");
        }

        User groupMember = identityService.createUserQuery().memberOfGroup(groupId).singleResult();
        if(groupMember == null) {
            throw new ItemNotFoundException("User with user id "+userId+" is not member of group "+groupId);
        }

        identityService.deleteMembership(userId, groupId);
        return "User "+userId+" deleted from group "+groupId;

    }
}
