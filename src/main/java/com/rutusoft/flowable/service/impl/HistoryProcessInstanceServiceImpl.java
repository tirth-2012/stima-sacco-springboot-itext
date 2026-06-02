package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.CustomerResponseDto;
import com.rutusoft.flowable.dto.ProcessInstanceDto;
import com.rutusoft.flowable.dto.ProcessInstancesResponseDto;
import com.rutusoft.flowable.dto.VariableInstanceDto;
import com.rutusoft.flowable.entity.Customer;
import com.rutusoft.flowable.enums.ActionType;
import com.rutusoft.flowable.enums.Status;
import com.rutusoft.flowable.repository.CustomerRepository;
import com.rutusoft.flowable.service.HistoryProcessInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class HistoryProcessInstanceServiceImpl implements HistoryProcessInstanceService {
    @Autowired
    private HistoryService historyService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public ProcessInstancesResponseDto completedProcessInstancesByInitiator(String initiator, int from, int to) {
        ProcessInstancesResponseDto processInstancesResponseDto = new ProcessInstancesResponseDto();
        List<ProcessInstanceDto> processInstanceDtos = new ArrayList<>();
        for (HistoricProcessInstance processInstance : historyService.createHistoricProcessInstanceQuery().finished().startedBy(initiator).orderByProcessInstanceStartTime().desc().listPage(from, to)) {
            processInstanceDtos.add(getProcessInstanceDto(processInstance));
        }

        long count = historyService.createHistoricProcessInstanceQuery().finished().startedBy(initiator).count();

        processInstancesResponseDto.setProcessInstances(processInstanceDtos);
        processInstancesResponseDto.setFrom(from);
        processInstancesResponseDto.setTo(to);
        processInstancesResponseDto.setTotal(count);
        return processInstancesResponseDto;
    }

    @Override
    public ProcessInstanceDto processInstanceByProcessInstanceId(String processInstanceId) {
        ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if(historicProcessInstance != null) {
            processInstanceDto = getProcessInstanceDto(historicProcessInstance);
        }

        return processInstanceDto;
    }

    @Override
    public ProcessInstancesResponseDto allProcessInstances(String initiator, int from, int to) {
        ProcessInstancesResponseDto processInstancesResponseDto = new ProcessInstancesResponseDto();
        List<ProcessInstanceDto> processInstanceDtos = new ArrayList<>();
        for (HistoricProcessInstance processInstance : historyService.createHistoricProcessInstanceQuery().startedBy(initiator).orderByProcessInstanceStartTime().desc().listPage(from, to)) {
            processInstanceDtos.add(getProcessInstanceDto(processInstance));
        }

        long count = historyService.createHistoricProcessInstanceQuery().finished().startedBy(initiator).count();

        processInstancesResponseDto.setProcessInstances(processInstanceDtos);
        processInstancesResponseDto.setFrom(from);
        processInstancesResponseDto.setTo(to);
        processInstancesResponseDto.setTotal(count);
        return processInstancesResponseDto;    }

    private ProcessInstanceDto getProcessInstanceDto(HistoricProcessInstance processInstance) {
        ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
        processInstanceDto.setProcessInstanceId(processInstance.getId());
        processInstanceDto.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        processInstanceDto.setProcessDefinitionName(processInstance.getProcessDefinitionName());
        processInstanceDto.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
        processInstanceDto.setBusinessKey(processInstance.getBusinessKey());
        processInstanceDto.setVersion(processInstance.getProcessDefinitionVersion());
        processInstanceDto.setStartUserId(fetchUserFullname(processInstance.getStartUserId()));
        processInstanceDto.setStartTime(processInstance.getStartTime());
        processInstanceDto.setEndTime(processInstance.getEndTime());
        processInstanceDto.setDeploymentId(processInstance.getDeploymentId());
        processInstanceDto.setEnded(true);
        if(processInstance.getEndTime() != null && processInstance.getDeleteReason() != null) {
            processInstanceDto.setStatus(Status.DECLINED.getLabel());
        } else if(processInstance.getEndTime() != null && processInstance.getDeleteReason() == null) {
            processInstanceDto.setStatus(Status.COMPLETED.getLabel());
        } else if(processInstance.getEndTime() == null) {
            processInstanceDto.setStatus(Status.IN_PROGRESS.getLabel());
        }

        processInstanceDto.setDeleteReason(processInstance.getDeleteReason());
        processInstanceDto.setSuspended(false);

        List<VariableInstanceDto> variableInstanceDtos = new ArrayList<>();
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).list();
        for(HistoricVariableInstance variableInstance : variableInstances) {
            VariableInstanceDto variableInstanceDto = new VariableInstanceDto();
            variableInstanceDto.setId(variableInstance.getId());
            variableInstanceDto.setName(variableInstance.getVariableName());
            variableInstanceDto.setValue(variableInstance.getValue());
            variableInstanceDto.setTypeName(variableInstance.getVariableTypeName());
            variableInstanceDto.setProcessInstanceId(variableInstance.getProcessInstanceId());
            //variableInstanceDto.setProcessDefinitionId(variableInstance.);
            variableInstanceDtos.add(variableInstanceDto);
        }

        processInstanceDto.setProcessVariables(variableInstanceDtos);
        return processInstanceDto;
    }

    private String fetchUserFullname(String userId) {
        List<User> users = identityService.createUserQuery().userId(userId).list();
        if(!users.isEmpty()) {
            return users.get(0).getFirstName() +" "+users.get(0).getLastName();
        }
        return "";
    }

    @Override
    public Map<String, Object> calculateLoanLimit(
            String initiator
    ) {

        Map<String, Object> result = new HashMap<>();

        try {

            ProcessInstancesResponseDto response =
                    completedProcessInstancesByInitiator(
                            initiator,
                            0,
                            100
                    );

            double utilizedAmount = 0.0;

            for (ProcessInstanceDto process :
                    response.getProcessInstances()) {

                if (!"Completed".equalsIgnoreCase(
                        process.getStatus())) {
                    continue;
                }

                if (process.getProcessVariables() == null) {
                    continue;
                }

                for (VariableInstanceDto variable :
                        process.getProcessVariables()) {

                    if ("total_loan_amount".equalsIgnoreCase(
                            variable.getName())) {

                        try {

                            Object value = variable.getValue();

                            if (value != null) {

                                utilizedAmount +=
                                        Double.parseDouble(
                                                value.toString()
                                        );
                            }

                        } catch (Exception e) {

                            log.error(
                                    "Error parsing totalLoanAmount",
                                    e
                            );
                        }
                    }
                }
            }

            Customer customer = customerRepository
                    .findByCifNumber(initiator)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Customer not found"
                            )
                    );

            Double totalLimit =
                    customer.getLoanAmountLimit() != null
                            ? customer.getLoanAmountLimit()
                            : 0.0;

            Double availableLimit =
                    totalLimit - utilizedAmount;

            if (availableLimit < 0) {
                availableLimit = 0.0;
            }

            result.put("cifNumber", initiator);
            result.put("customerName", customer.getFullName());
            result.put("totalLoanLimit", totalLimit);
            result.put("utilizedLoanAmount", utilizedAmount);
            result.put("availableLoanLimit", availableLimit);

            return result;

        } catch (Exception e) {

            log.error(
                    "Error calculating loan limit",
                    e
            );

            result.put("message", "Error calculating loan limit");

            return result;
        }
    }
}
