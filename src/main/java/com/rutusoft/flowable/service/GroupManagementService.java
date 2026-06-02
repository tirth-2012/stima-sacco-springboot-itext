package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.GroupDto;

import java.util.List;

public interface GroupManagementService {
    String createNewGroup(GroupDto groupDto);
    List<GroupDto> listGroups();
    GroupDto getGroupByGroupId(String groupId);
    String addMemberToGroup(String groupId, String userId);
    String deleteMemberFromGroup(String groupId, String userId);
}
