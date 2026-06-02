package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.*;

import javax.print.DocFlavor;
import java.util.List;
import java.util.Map;

public interface TaskInstanceService {
    TaskInstancesResponseDto getAllActiveTasks(int from, int to);
    TaskInstancesResponseDto getActiveTasksByAssignee(String assignee, int from, int to);
    TaskInstancesResponseDto getActiveTasksByCandidateGroups(List<String> candidateGroups, int from, int to);
    TaskInstancesResponseDto getAllActiveTasksByProcessInstanceId(String processInstanceId, int from, int to);
    TaskInstanceDto getTaskById(String taskId);
    String claimTask(String taskId, String assignee);
    String unClaimTask(String taskId);
    String saveTask(TaskInstanceUpdateDto instanceUpdateDto);
    String completeTask(String taskId, Map<String, Object> variables);
    String addCandidateUser(String taskId, String candidateUser);
    String addCandidateGroup(String taskId, String candidateGroup);
    String changeAssignee(String taskId, String assignee);
}
