package com.rutusoft.flowable.service;

import com.rutusoft.flowable.entity.TaskActionLog;

import java.util.List;

public interface TaskActionLogService {
    List<TaskActionLog> fetchTaskActionLogsByProcessInstanceId(String processInstanceId);
}
