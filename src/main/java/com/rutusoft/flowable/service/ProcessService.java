package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.ProcessInstancesResponseDto;
import com.rutusoft.flowable.dto.TasksResponseDto;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ProcessService {
    String startProcess(String processDefinitionKey, Map<String, Object> processVariables);
    ProcessInstancesResponseDto activeProcessInstances(int from, int to);
    ProcessInstancesResponseDto activeProcessInstancesByInitiator(String initiator, int from, int to);
    ProcessInstancesResponseDto listProcessInstancesByKey(String processDefinitionKey, int from, int to);
    String modifyProcessInstanceState(String processInstanceId, String currentActivityId, String newActivityId, Map<String, Object> variables);
    ProcessInstancesResponseDto getRecentProcessInstances(String segment, String referenceId);
    void getProcessDiagram(String processInstanceId, HttpServletResponse response);

    TasksResponseDto getActiveApplicationsGroupedByTask(String processDefinitionKey);
    String terminateProcessInstance(String processInstanceId, String reason);
}
