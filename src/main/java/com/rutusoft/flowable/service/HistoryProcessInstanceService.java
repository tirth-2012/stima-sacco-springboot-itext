package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.ProcessInstanceDto;
import com.rutusoft.flowable.dto.ProcessInstancesResponseDto;

import java.util.Map;

public interface HistoryProcessInstanceService {

    ProcessInstancesResponseDto completedProcessInstancesByInitiator(String initiator, int from, int to);
    ProcessInstanceDto processInstanceByProcessInstanceId(String processInstanceId);

    Map<String, Object> calculateLoanLimit(String initiator);
    ProcessInstancesResponseDto allProcessInstances(String initiator, int from, int to);
}
