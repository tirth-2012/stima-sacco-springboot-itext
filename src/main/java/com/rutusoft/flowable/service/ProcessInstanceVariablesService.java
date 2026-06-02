package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.VariableInstanceDto;

import java.util.List;
import java.util.Map;

public interface ProcessInstanceVariablesService {
    List<VariableInstanceDto> getProcessInstanceVariables(String processInstanceId);
    Map<String, Object> getProcessInstanceVariablesMap(String processInstanceId);
    String upsertProcessInstanceVariables(String processInstanceId, Map<String, Object> variables);
    VariableInstanceDto getProcessInstanceVariable(String processInstanceId, String variableName);
    String deleteProcessInstanceVariable(String processInstanceId, String variableName);
}
