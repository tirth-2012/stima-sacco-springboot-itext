package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.TaskInstanceDto;
import com.rutusoft.flowable.dto.TaskInstancesResponseDto;

import java.util.List;

public interface HistoryTaskService {
    TaskInstancesResponseDto getCompletedTaskByUserId(String userId, int from, int to);
    TaskInstancesResponseDto getAllTasks(String assignee, List<String> candidateGroups, int from, int to);
    TaskInstancesResponseDto getTaskInstancesByProcessInstanceId(String processInstanceId, int from, int to);

    TaskInstanceDto getTaskById(String taskId);

    TaskInstancesResponseDto getAllCompletedTasksByProcessInstanceId(String processInstanceId, int from, int to);
}
