package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.VariableInstanceDto;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.service.ProcessInstanceVariablesService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProcessInstanceVariablesServiceImpl implements ProcessInstanceVariablesService {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Override
    public List<VariableInstanceDto> getProcessInstanceVariables(String processInstanceId) {
        List<VariableInstanceDto> variableInstanceDtos = new ArrayList<>();
        for(HistoricVariableInstance variableInstance : historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list()) {
            VariableInstanceDto variableInstanceDto = new VariableInstanceDto();
            variableInstanceDto.setId(variableInstance.getId());
            variableInstanceDto.setName(variableInstance.getVariableName());
            variableInstanceDto.setProcessInstanceId(variableInstance.getProcessInstanceId());
            variableInstanceDto.setTypeName(variableInstance.getVariableTypeName());
            variableInstanceDto.setValue(variableInstance.getValue());
            variableInstanceDtos.add(variableInstanceDto);
        }
        return variableInstanceDtos;
    }

    @Override
    public Map<String, Object> getProcessInstanceVariablesMap(String processInstanceId) {

        if (processInstanceId == null || processInstanceId.trim().isEmpty()) {
            throw new IllegalArgumentException("processInstanceId must not be null or empty");
        }

        Map<String, Object> processVariables = new HashMap<>(16);

        try {
            ProcessInstance processInstance = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (processInstance != null) {
                // Active process
                List<VariableInstance> variableInstances = runtimeService
                        .createVariableInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .list();

                for (VariableInstance variableInstance : variableInstances) {
                    putSafe(processVariables, variableInstance.getName(), variableInstance.getValue());
                }

            } else {
                // Completed / historic process
                List<HistoricVariableInstance> variableInstances = historyService
                        .createHistoricVariableInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .list();

                for (HistoricVariableInstance variableInstance : variableInstances) {
                    putSafe(processVariables, variableInstance.getVariableName(), variableInstance.getValue());
                }
            }

        } catch (Exception ex) {
            // Replace with your logging framework (SLF4J recommended)
            log.error("Error fetching variables for processInstanceId: " + processInstanceId);
            throw new RuntimeException("Failed to fetch process variables", ex);
        }

        return processVariables;
    }

    @Override
    public String upsertProcessInstanceVariables(String processInstanceId,
                                                 Map<String, Object> variables) {

        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (processInstance == null) {
            throw new ValidationException(
                    "Process instance not found or already completed: " + processInstanceId
            );
        }

        try {
            runtimeService.setVariables(processInstanceId, variables);

            // Optional audit log
            log.info("Variables updated for processInstanceId={}, variables={}",
                    processInstanceId, variables);

            return "Process variables updated successfully";

        } catch (Exception e) {
            log.error("Failed to update variables for processInstanceId={}", processInstanceId, e);
            throw new RuntimeException("Error updating process variables", e);
        }
    }

    @Override
    public VariableInstanceDto getProcessInstanceVariable(String processInstanceId, String variableName) {
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).variableName(variableName).list();
        VariableInstanceDto variableInstanceDto = new VariableInstanceDto();
        if(!variableInstances.isEmpty()) {
            HistoricVariableInstance variableInstance = variableInstances.get(0);
            variableInstanceDto.setId(variableInstance.getId());
            variableInstanceDto.setName(variableInstance.getVariableName());
            variableInstanceDto.setProcessInstanceId(variableInstance.getProcessInstanceId());
            variableInstanceDto.setTypeName(variableInstance.getVariableTypeName());
            variableInstanceDto.setValue(variableInstance.getValue());
        }
        return variableInstanceDto;
    }

    @Override
    public String deleteProcessInstanceVariable(String processInstanceId, String variableName) {
        runtimeService.removeVariable(processInstanceId, variableName);
        return "Process instance variable removed successfully";
    }

    private void putSafe(Map<String, Object> map, String key, Object value) {
        if (key == null) {
            return;
        }

        try {
            map.put(key, value);
        } catch (Exception ex) {
            // Avoid breaking whole response due to one bad variable
            System.err.println("Failed to deserialize variable: " + key);
            map.put(key, null);
        }
    }
}
