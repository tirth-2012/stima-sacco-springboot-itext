package com.rutusoft.flowable.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutusoft.flowable.dto.*;
import com.rutusoft.flowable.entity.Guarantor;
import com.rutusoft.flowable.entity.ProcessInstanceAudit;
import com.rutusoft.flowable.entity.TaskActionLog;
import com.rutusoft.flowable.enums.ActionType;
import com.rutusoft.flowable.enums.Status;
import com.rutusoft.flowable.exception.ServerException;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.repository.ProcessInstanceAuditRepository;
import com.rutusoft.flowable.repository.TaskActionLogRepository;
import com.rutusoft.flowable.service.*;
import com.rutusoft.flowable.utility.MailNotificationUtil;
import com.rutusoft.flowable.utility.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {
    @Value("${admin.email:sg.vadaviya@gmail.com}")
    private String adminEmail;

    @Value("${admin.notification-enabled:false}")
    private boolean notificationEnabled;

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final RepositoryService repositoryService;
    private final ProcessEngine processEngine;
    private final SecurityUtil securityUtil;
    private final ProcessInstanceSequenceService sequenceService;
    private final TaskActionLogRepository taskActionLogRepository;
    private final IdentityService identityService;
    private final MailNotificationUtil mailNotificationUtil;
    private final LoanApplicationService loanApplicationService;
    private final LoanFinancialDetailsService loanFinancialDetailsService;
    private final GuarantorService guarantorService;
    private final ProcessInstanceAuditRepository processInstanceAuditRepository;

    @Override
    public String startProcess(
            String processDefinitionKey,
            Map<String, Object> processVariables
    ) {

        ObjectMapper mapper = new ObjectMapper();

        String currentUserId = securityUtil.getCurrentUserId();

        Authentication.setAuthenticatedUserId(currentUserId);

        String productName =
                processVariables.get("product_name") == null
                        ? ""
                        : processVariables.get("product_name").toString();

        int currentYear = Year.now().getValue();

        String productCode = generateProductCode(productName);

        String businessKey =
                "STIMA-LOS"
                        + "-" + currentYear
                        + "-" + String.format(
                        "%05d",
                        sequenceService.getNextValue()
                );

        processVariables.put("referenceId", businessKey);

        if (processVariables.get("requester") != null) {

            String requester =
                    processVariables.get("requester").toString();

            processVariables.put(
                    "requesterFullName",
                    fetchUserFullname(requester)
            );

        } else {

            processVariables.put("requesterFullName", "");
        }

        // ------------------------------------------------------------
        // START FLOWABLE PROCESS
        // ------------------------------------------------------------
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(
                        processDefinitionKey,
                        businessKey,
                        processVariables
                );

        // ------------------------------------------------------------
        // SAVE INTO LOAN_APPLICATION TABLE
        // ------------------------------------------------------------
        LoanApplicationRequestDto applicationDto =
                new LoanApplicationRequestDto();

        applicationDto.setReferenceId(businessKey);
        applicationDto.setBusinessKey(businessKey);

        applicationDto.setProcessInstanceId(
                processInstance.getProcessInstanceId()
        );

        applicationDto.setProcessDefinitionId(
                processInstance.getProcessDefinitionId()
        );

        applicationDto.setCustomerId(
                getLong(processVariables.get("customer_id"))
        );

        applicationDto.setProductId(
                getLong(processVariables.get("product_id"))
        );

        applicationDto.setRequester(
                getString(processVariables.get("requester"))
        );

        applicationDto.setRequesterFullName(
                getString(processVariables.get("requesterFullName"))
        );

        applicationDto.setRmUser(
                getString(processVariables.get("rm_user"))
        );

        applicationDto.setApplicationByCustomer(
                getBoolean(processVariables.get("application_by_customer"))
        );

        applicationDto.setProductType(
                getString(processVariables.get("product_type"))
        );

        applicationDto.setProductName(
                getString(processVariables.get("product_name"))
        );

        applicationDto.setLoanPurposeDescription(
                getString(processVariables.get("loan_purpose_description"))
        );

        applicationDto.setAssetDescription(
                getString(processVariables.get("asset_description"))
        );

        applicationDto.setCostPrice(
                getBigDecimal(processVariables.get("cost_price"))
        );

        applicationDto.setProfitRate(
                getBigDecimal(processVariables.get("profit_rate"))
        );

        applicationDto.setProfitAmount(
                getBigDecimal(processVariables.get("profit_amount"))
        );

        applicationDto.setTotalLoanAmount(
                getBigDecimal(processVariables.get("total_sale_price"))
        );

        applicationDto.setFinancingTenor(
                getInteger(processVariables.get("financing_tenure"))
        );

        applicationDto.setPaymentStructure(
                getString(processVariables.get("payment_structure"))
        );

        applicationDto.setMonthlyInstallment(
                getBigDecimal(processVariables.get("monthly_installment"))
        );

        applicationDto.setProposedInstalment(
                getBigDecimal(processVariables.get("proposed_instalment"))
        );

        applicationDto.setAfterThisFacility(
                getBigDecimal(processVariables.get("after_this_facility"))
        );

        applicationDto.setDisbursementType(
                getString(processVariables.get("disbursement_type"))
        );

        applicationDto.setBankName(
                getString(processVariables.get("bank_name"))
        );

        applicationDto.setBranchName(
                getString(processVariables.get("branch_name"))
        );

        applicationDto.setAccountNumber(
                getString(processVariables.get("account_number"))
        );

        applicationDto.setAccountType(
                getString(processVariables.get("account_type"))
        );

        applicationDto.setSwiftCode(
                getString(processVariables.get("swift_code"))
        );

        applicationDto.setCustomerCategory(
                getString(processVariables.get("customer_category"))
        );

        applicationDto.setBusinessSector(
                getString(processVariables.get("business_sector"))
        );

        applicationDto.setCurrentStage("Application Initiated");

        applicationDto.setStatus(Status.IN_PROGRESS.getCode());

        LoanApplicationResponseDto savedApplication =
                loanApplicationService.createApplication(
                        applicationDto
                );

        // ------------------------------------------------------------
        // SAVE FINANCIAL DETAILS TABLE
        // ------------------------------------------------------------
        LoanFinancialDetailsRequestDto financialDto =
                new LoanFinancialDetailsRequestDto();

        financialDto.setLoanApplicationId(
                savedApplication.getId()
        );

        financialDto.setMonthlyNetIncome(
                getBigDecimal(processVariables.get("monthly_net_income"))
        );

        financialDto.setMonthlyBusinessRevenue(
                getBigDecimal(
                        processVariables.get(
                                "monthly_business_revenue"
                        )
                )
        );

        financialDto.setAnnualTurnover(
                getBigDecimal(processVariables.get("annual_turnover"))
        );

        financialDto.setYearsOfBusiness(
                getInteger(processVariables.get("years_of_business"))
        );

        financialDto.setExistingMonthlyObligations(
                getBigDecimal(
                        processVariables.get(
                                "existing_monthly_obligations"
                        )
                )
        );

        financialDto.setNumberOfExistingFacilities(
                getInteger(
                        processVariables.get(
                                "number_of_existing_facilities"
                        )
                )
        );

        financialDto.setDebtServiceRatio(
                getBigDecimal(
                        processVariables.get("debt_service_ratio")
                )
        );

        financialDto.setCoverageRatio(
                getBigDecimal(
                        processVariables.get("coverage_ratio")
                )
        );

        loanFinancialDetailsService
                .createFinancialDetails(financialDto);

        Authentication.setAuthenticatedUserId(null);

        return processInstance.getProcessInstanceId();
    }


    @Override
    public ProcessInstancesResponseDto activeProcessInstances(int from, int to) {
        ProcessInstancesResponseDto processInstancesResponseDto = new ProcessInstancesResponseDto();
        List<ProcessInstanceDto> processInstanceDtos = new ArrayList<>();
        for (ProcessInstance processInstance : runtimeService.createProcessInstanceQuery().active().orderByStartTime().desc().listPage(from, to)) {
            processInstanceDtos.add(getProcessInstanceDto(processInstance));
        }

        long count = runtimeService.createProcessInstanceQuery().active().count();

        processInstancesResponseDto.setProcessInstances(processInstanceDtos);
        processInstancesResponseDto.setFrom(from);
        processInstancesResponseDto.setTo(to);
        processInstancesResponseDto.setTotal(count);
        return processInstancesResponseDto;
    }

    @Override
    public ProcessInstancesResponseDto activeProcessInstancesByInitiator(String initiator, int from, int to) {
        ProcessInstancesResponseDto processInstancesResponseDto = new ProcessInstancesResponseDto();
        List<ProcessInstanceDto> processInstanceDtos = new ArrayList<>();
        for (ProcessInstance processInstance : runtimeService.createProcessInstanceQuery().active().startedBy(initiator).orderByStartTime().desc().listPage(from, to)) {
            processInstanceDtos.add(getProcessInstanceDto(processInstance));
        }

        long count = runtimeService.createProcessInstanceQuery().active().startedBy(initiator).count();

        processInstancesResponseDto.setProcessInstances(processInstanceDtos);
        processInstancesResponseDto.setFrom(from);
        processInstancesResponseDto.setTo(to);
        processInstancesResponseDto.setTotal(count);
        return processInstancesResponseDto;
    }

    @Override
    public ProcessInstancesResponseDto listProcessInstancesByKey(String processDefinitionKey, int from, int to) {
        ProcessInstancesResponseDto processInstancesResponseDto = new ProcessInstancesResponseDto();
        List<ProcessInstanceDto> processInstanceDtos = new ArrayList<>();
        for (ProcessInstance processInstance : runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).active().orderByStartTime().desc().listPage(from, to)) {
            processInstanceDtos.add(getProcessInstanceDto(processInstance));
        }

        long count = runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).active().count();

        processInstancesResponseDto.setProcessInstances(processInstanceDtos);
        processInstancesResponseDto.setFrom(from);
        processInstancesResponseDto.setTo(to);
        processInstancesResponseDto.setTotal(count);
        return processInstancesResponseDto;
    }

    @Override
    public String modifyProcessInstanceState(String processInstanceId, String currentActivityId, String newActivityId, Map<String, Object> variables) {
        log.info("Modifying process instance : {}, current activity : {}, new activity : {}", processInstanceId, currentActivityId, newActivityId);

        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).list();
        if(processInstances.isEmpty()) {
            throw new ValidationException("Process instance does not exist");
        }

        List<Task> currentTasks = taskService.createTaskQuery().processInstanceId(processInstanceId).taskDefinitionKey(currentActivityId).active().list();
        if(currentTasks.isEmpty()) {
            throw new ValidationException("Current task does not exist for a given process instance");
        }
        List<HistoricTaskInstance> newHistoricTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskDefinitionKey(newActivityId).finished().list();
        log.info("historicTaskInstances : {}", newHistoricTaskInstances.size());

        String taskId = currentTasks.get(0).getId();
        String currentTaskName = currentTasks.get(0).getName();
        String action = variables.get("action") == null ? "" : variables.get("action").toString();
        log.info("action : {}", action);

        String stepBackToStage = "";
        Map<String, Object> processVariables = new HashMap<>();
        if(!newHistoricTaskInstances.isEmpty()) {
            HistoricTaskInstance task = newHistoricTaskInstances.get(0);
            stepBackToStage = task.getName();
            variables.put("step_back_to", stepBackToStage);
            log.info("Current tasks : {}", currentTasks.size());
            if(!currentTasks.isEmpty()) {
                taskService.setVariablesLocal(currentTasks.get(0).getId(), variables);
            }
        } else {
            for(Map.Entry<String, Object> entry: variables.entrySet()) {
                processVariables.put(newActivityId+"_"+entry.getKey(), entry.getValue().toString());
            }
            processVariables.put("rejected_from_stage", newActivityId);
        }

        String taskAction = variables.get("action") == null ? "" : variables.get("action").toString();
        String taskActionReason = variables.get("action_reason") == null ? "" : variables.get("action_reason").toString();
        String taskActionBy = variables.get("action_by") == null ? "" : variables.get("action_by").toString();
        String taskActionByFullname = "";
        if(!taskAction.equals("")){
            List<User> users = identityService.createUserQuery().userId(taskActionBy).list();
            if(!users.isEmpty()) {
                taskActionByFullname = users.get(0).getFirstName() + " "+users.get(0).getLastName();
            }
        }
        persistTaskActionLog(processInstanceId, taskId, currentTaskName, taskActionBy, taskActionByFullname, taskAction, taskActionReason, stepBackToStage);

        try {
            if(action.equalsIgnoreCase("Sent back")) {
                processVariables.put("step_back_from_stage", currentTaskName);
            }
            else {
                processVariables.put("rejected_from_stage", currentTaskName);
            }

            for(Map.Entry<String, Object> entry: variables.entrySet()) {
                if(action.equalsIgnoreCase("Sent back")) {
                    processVariables.put(currentActivityId + "_step_back_" + entry.getKey(), entry.getValue().toString());
                    processVariables.put(newActivityId + "_step_back_" + entry.getKey(), entry.getValue().toString());
                }
                else if(action.equalsIgnoreCase("Rejected")) {
                    processVariables.put(currentActivityId + "_rejected_" + entry.getKey(), entry.getValue().toString());
                    processVariables.put(newActivityId + "_rejected_" + entry.getKey(), entry.getValue().toString());
                }
                else {
                    // TO DO
                }
            }
            runtimeService.setVariables(processInstanceId, processVariables);

            String currentTaskId = currentTasks.get(0).getId();
            taskService.setVariablesLocal(currentTaskId, processVariables);
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(processInstanceId)
                    .moveActivityIdTo(currentActivityId, newActivityId)
                    .changeState();

            log.info("Moved process {} from {} to {}", processInstanceId, currentActivityId, newActivityId);

        } catch (Exception e) {
            log.error("Failed to move process {} from {} to {}",
                    processInstanceId, currentActivityId, newActivityId, e);
            throw new RuntimeException("Process state change failed", e);
        }
        return "Application request send back to selected activity";
    }

    @Override
    public ProcessInstancesResponseDto getRecentProcessInstances(String segment, String referenceId) {

        ProcessInstancesResponseDto response = new ProcessInstancesResponseDto();
        List<ProcessInstanceDto> dtos = new ArrayList<>();

        try {
            // ✅ Base query
            HistoricProcessInstanceQuery query =
                    historyService.createHistoricProcessInstanceQuery();

            // ✅ Apply filters (if present)
            if (segment != null && !segment.isBlank()) {
                query.variableValueLikeIgnoreCase("product_type", segment);
            }

            if (referenceId != null && !referenceId.isBlank()) {
                query.variableValueLikeIgnoreCase("referenceId", referenceId);
            }

            // ✅ Sort latest first
            List<HistoricProcessInstance> instances =
                    query.orderByProcessInstanceStartTime()
                            .desc()
                            .listPage(0, 15);

            // ✅ Mapping
            for (HistoricProcessInstance processInstance : instances) {
                dtos.add(getHistoricalProcessInstanceDto(processInstance));
            }

            response.setProcessInstances(dtos);

        } catch (Exception ex) {
            log.error("Error fetching recent process instances. segment={}, referenceId={}",
                    segment, referenceId, ex);
            throw new RuntimeException("Unable to fetch process instances");
        }

        return response;
    }

    @Override
    public void getProcessDiagram(String processInstanceId, HttpServletResponse response) {
        InputStream inputStream = null;

        try {
            // 1. Get historic process instance
            HistoricProcessInstance historicProcessInstance =
                    historyService.createHistoricProcessInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .singleResult();

            if (historicProcessInstance == null) {
                throw new RuntimeException("Process instance not found: " + processInstanceId);
            }

            String processDefinitionId = historicProcessInstance.getProcessDefinitionId();

            // 2. Get BPMN model
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

            // 3. Get completed activities
            List<HistoricActivityInstance> historicActivityInstances =
                    historyService.createHistoricActivityInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .orderByHistoricActivityInstanceStartTime()
                            .asc()
                            .list();

            List<String> completedActivities = new ArrayList<>();
            for (HistoricActivityInstance hai : historicActivityInstances) {
                if (hai.getEndTime() != null) {
                    completedActivities.add(hai.getActivityId());
                }
            }

            // 4. Get active activities (SAFE handling)
            List<String> activeActivities = new ArrayList<>();

            ProcessInstance processInstance =
                    runtimeService.createProcessInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .singleResult();

            if (processInstance != null) {
                // ✅ Active process
                activeActivities = runtimeService.getActiveActivityIds(processInstanceId);
            } else {
                // ✅ Completed process → fallback to history
                List<HistoricActivityInstance> unfinishedActivities =
                        historyService.createHistoricActivityInstanceQuery()
                                .processInstanceId(processInstanceId)
                                .unfinished()
                                .list();

                for (HistoricActivityInstance hai : unfinishedActivities) {
                    activeActivities.add(hai.getActivityId());
                }
            }

            // 5. Generate diagram
            ProcessEngineConfiguration processEngineConfiguration =
                    processEngine.getProcessEngineConfiguration();

            ProcessDiagramGenerator diagramGenerator =
                    processEngineConfiguration.getProcessDiagramGenerator();

            inputStream = diagramGenerator.generateDiagram(
                    bpmnModel,
                    "png",
                    activeActivities,        // Green
                    completedActivities,     // Completed path
                    processEngineConfiguration.getActivityFontName(),
                    processEngineConfiguration.getLabelFontName(),
                    processEngineConfiguration.getAnnotationFontName(),
                    processEngineConfiguration.getClassLoader(),
                    1.0,
                    true
            );

            // 6. Write response
            response.setContentType("image/png");
            IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();

        } catch (Exception e) {
            log.error("Error occurred while fetching diagram details: ", e);
            throw new RuntimeException("Failed to generate process diagram", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("Error closing input stream", e);
                }
            }
        }
    }

    @Override
    public TasksResponseDto getActiveApplicationsGroupedByTask(String processDefinitionKey) {

        // 1. Get latest process definition
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .latestVersion()
                .singleResult();

        // 2. Get BPMN model
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        Process process = bpmnModel.getMainProcess();

        List<UserTask> userTasks = process.getFlowElements().stream()
                .filter(e -> e instanceof UserTask)
                .map(e -> (UserTask) e)
                .collect(Collectors.toList());

        // 3. Active tasks
        List<Task> activeTasks = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .active()
                .list();

        Map<String, List<ApplicationDto>> grouped = new HashMap<>();

        for (Task task : activeTasks) {

            String key = task.getTaskDefinitionKey();
            String processInstanceId = task.getProcessInstanceId();

            ApplicationDto app = fetchApplicationData(processInstanceId);
            app.setProcessInstanceId(processInstanceId);

            grouped.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(app);
        }

        // 4. Build response (INCLUDING ZERO COUNT TASKS)
        List<TaskGroupDto> taskGroups = new ArrayList<>();

        for (UserTask userTask : userTasks) {

            String key = userTask.getId();
            String name = userTask.getName();

            List<ApplicationDto> applications =
                    grouped.getOrDefault(key, new ArrayList<>());

            TaskGroupDto dto = new TaskGroupDto();
            dto.setTaskDefinitionKey(key);
            dto.setName(name);
            dto.setApplications(applications);
            dto.setCount(applications.size());

            taskGroups.add(dto);
        }

        TasksResponseDto response = new TasksResponseDto();
        response.setTasks(taskGroups);

        return response;
    }

    @Override
    public String terminateProcessInstance(String processInstanceId, String reason) {

        if (processInstanceId == null || processInstanceId.isBlank()) {
            throw new ValidationException("ProcessInstanceId must not be null or empty");
        }

        try {
            ProcessInstance processInstance = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            if (processInstance == null) {
                throw new ValidationException("Application is already completed, rejected or does not exist");
            }


            Execution execution = runtimeService
                    .createExecutionQuery()
                    .processInstanceId(processInstanceId)
                    .onlyProcessInstanceExecutions()
                    .singleResult();

            String executionId = execution.getId();
            Map<String, Object> variables = runtimeService.getVariables(executionId);

            runtimeService.deleteProcessInstance(processInstanceId,
                    reason != null ? reason : "Terminated by user");

            log.info("Process instance {} terminated successfully. Reason: {}", processInstanceId, reason);

            loanApplicationService.updateApplicationStatus(
                    processInstanceId,
                    Status.DECLINED.getCode()
            );

            applicationDeclineNotification(variables, processInstanceId, reason);

            //Persist audit
            ProcessInstanceAudit processInstanceAudit = new ProcessInstanceAudit();
            processInstanceAudit.setProcessInstanceId(execution.getProcessInstanceId());
            processInstanceAudit.setActionTime(new Date());
            processInstanceAudit.setTitle("Application Declined");
            processInstanceAudit.setAction(ActionType.DECLINED.getLabel());
            processInstanceAudit.setActionReason(reason);

            if(variables.get("requester") != null) {
                processInstanceAudit.setActionedBy(securityUtil.getCurrentUserFullName());
            }

            processInstanceAuditRepository.save(processInstanceAudit);
            return "Application terminated successfully";

        } catch (FlowableObjectNotFoundException e) {
            throw new ValidationException("Process instance not found");

        } catch (FlowableException e) {
            log.error("Flowable error while terminating process instance {}", processInstanceId, e);
            throw new ServerException("Error while terminating process instance");

        } catch (Exception e) {
            log.error("Unexpected error while terminating process instance {}", processInstanceId, e);
            throw new ServerException("Unexpected error occurred");
        }
    }

    private ProcessInstanceDto getProcessInstanceDto(ProcessInstance processInstance) {
        ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
        processInstanceDto.setProcessInstanceId(processInstance.getProcessInstanceId());
        processInstanceDto.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        processInstanceDto.setProcessDefinitionName(processInstance.getProcessDefinitionName());
        processInstanceDto.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
        processInstanceDto.setBusinessKey(processInstance.getBusinessKey());
        processInstanceDto.setVersion(processInstance.getProcessDefinitionVersion());
        processInstanceDto.setStartUserId(fetchUserFullname(processInstance.getStartUserId()));
        processInstanceDto.setStartTime(processInstance.getStartTime());
        processInstanceDto.setDeploymentId(processInstance.getDeploymentId());
        processInstanceDto.setEnded(processInstance.isEnded());
        processInstanceDto.setSuspended(processInstance.isSuspended());

        List<VariableInstanceDto> variableInstanceDtos = new ArrayList<>();
        List<VariableInstance> variableInstances = runtimeService.createVariableInstanceQuery().processInstanceId(processInstance.getProcessInstanceId()).list();
        for(VariableInstance variableInstance : variableInstances) {
            VariableInstanceDto variableInstanceDto = new VariableInstanceDto();
            variableInstanceDto.setId(variableInstance.getId());
            variableInstanceDto.setName(variableInstance.getName());
            variableInstanceDto.setValue(variableInstance.getValue());
            variableInstanceDto.setTypeName(variableInstance.getTypeName());
            variableInstanceDto.setProcessInstanceId(variableInstance.getProcessInstanceId());
            variableInstanceDto.setProcessDefinitionId(variableInstance.getProcessDefinitionId());
            variableInstanceDtos.add(variableInstanceDto);
        }

        processInstanceDto.setProcessVariables(variableInstanceDtos);
        return processInstanceDto;
    }

    private ProcessInstanceDto getHistoricalProcessInstanceDto(HistoricProcessInstance processInstance) {
        ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
        processInstanceDto.setProcessInstanceId(processInstance.getId());
        processInstanceDto.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        processInstanceDto.setProcessDefinitionName(processInstance.getProcessDefinitionName());
        processInstanceDto.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
        processInstanceDto.setBusinessKey(processInstance.getBusinessKey());
        processInstanceDto.setVersion(processInstance.getProcessDefinitionVersion());
        processInstanceDto.setStartUserId(fetchUserFullname(processInstance.getStartUserId()));
        processInstanceDto.setStartTime(processInstance.getStartTime());
        processInstanceDto.setDeploymentId(processInstance.getDeploymentId());
        if(processInstance.getEndTime() != null)
            processInstanceDto.setEnded(true);
        else {
            processInstanceDto.setEnded(false);
        }
        if(processInstance.getEndTime() != null && processInstance.getDeleteReason() != null) {
            processInstanceDto.setStatus(Status.DECLINED.getLabel());
        } else if(processInstance.getEndTime() != null && processInstance.getDeleteReason() == null) {
            processInstanceDto.setStatus(Status.COMPLETED.getLabel());
        } else if(processInstance.getEndTime() == null) {
            processInstanceDto.setStatus(Status.IN_PROGRESS.getLabel());
        }

        List<VariableInstanceDto> variableInstanceDtos = new ArrayList<>();
        List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).list();
        for(HistoricVariableInstance variableInstance : variableInstances) {
            VariableInstanceDto variableInstanceDto = new VariableInstanceDto();
            variableInstanceDto.setId(variableInstance.getId());
            variableInstanceDto.setName(variableInstance.getVariableName());
            variableInstanceDto.setValue(variableInstance.getValue());
            variableInstanceDto.setTypeName(variableInstance.getVariableTypeName());
            variableInstanceDto.setProcessInstanceId(variableInstance.getProcessInstanceId());
            variableInstanceDtos.add(variableInstanceDto);
        }

        processInstanceDto.setProcessVariables(variableInstanceDtos);
        return processInstanceDto;
    }

    private void persistTaskActionLog(String processInstanceId, String taskId, String taskName, String actionBy, String taskActionByFullname, String actionName, String actionComment, String stepBackToStage) {
        log.info("ACTION NAME : {}", actionName);
        ActionType actionType;
        if(actionName.equalsIgnoreCase("Rejected")) {
            actionType = ActionType.REJECT;
        }
        else if(actionName.equalsIgnoreCase("Sent back")){
            actionType = ActionType.SEND_BACK;
        }
        else {
            actionType = ActionType.SUBMIT;
        }
        TaskActionLog taskActionLog = TaskActionLog.builder()
                .processInstanceId(processInstanceId)
                .taskId(taskId)
                .taskName(taskName)
                .action(actionType)
                .actionBy(actionBy)
                .actionByName(taskActionByFullname)
                .comments(actionComment)
                .stepBackToStage(stepBackToStage)
                .build();
        taskActionLogRepository.save(taskActionLog);
        log.info("Persisting task action log : {}", taskActionLog);
    }

    private ApplicationDto fetchApplicationData(String processInstanceId) {
        Map<String, Object> vars = runtimeService.getVariables(processInstanceId);

        ApplicationDto dto = new ApplicationDto();
        dto.setReferenceId((String) vars.get("referenceId"));
        dto.setFullname((String) vars.get("full_name"));
        dto.setLoanAmount(Integer.parseInt(vars.get("cost_price").toString()));
        dto.setProductType((String) vars.get("product_type"));
        return dto;
    }

    private String fetchUserFullname(String userId) {
        if(userId != null) {
            List<User> users = identityService.createUserQuery().userId(userId).list();
            if (!users.isEmpty()) {
                return users.get(0).getFirstName() + " " + users.get(0).getLastName();
            }
        }
        return "";
    }

    private String generateProductCode(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return "NA";
        }

        // Remove special characters except space
        String cleaned = productName.replaceAll("[^a-zA-Z0-9 ]", " ");

        // Normalize spaces
        cleaned = cleaned.trim().replaceAll("\\s+", " ");

        StringBuilder code = new StringBuilder();

        for (String word : cleaned.split(" ")) {
            if (!word.isEmpty()) {
                code.append(Character.toUpperCase(word.charAt(0)));
            }
        }

        return code.toString();
    }

    public void applicationDeclineNotification(Map<String, Object> variables, String processInstanceId, String reason) {
        log.info("Sending application declined notification");

        String requester = getValue(variables, "requester");
        User requesterUser = identityService
                .createUserQuery()
                .userId(requester)
                .singleResult();

        String requesterEmailId = null;
        if (requesterUser != null && requesterUser.getEmail() != null) {
            requesterEmailId = requesterUser.getEmail();
        }


        Context context = new Context();

        // ===============================
        // 👤 CUSTOMER
        // ===============================
        context.setVariable("fullName", getValue(variables,"full_name"));
        context.setVariable("email", getValue(variables,"email_id"));
        context.setVariable("cifNumber", getValue(variables,"cif_number"));
        context.setVariable("requester", getValue(variables,"requester"));
        context.setVariable("gender", getValue(variables,"gender"));
        context.setVariable("dob", getValue(variables,"date_of_birth"));
        context.setVariable("loanAmount", getValue(variables,"cost_price"));
        context.setVariable("referenceId", getValue(variables,"referenceId"));
        context.setVariable("declinedReason", reason);

        context.setVariable("nationalId", getValue(variables,"national_id"));
        context.setVariable("kraPin", getValue(variables,"kra_id"));
        context.setVariable("nationality", getValue(variables,"nationality"));
        context.setVariable("maritalStatus", getValue(variables,"marital_status"));

        context.setVariable("mobile", getValue(variables,"mobile_number"));
        context.setVariable("physicalAddress", getValue(variables,"physical_address"));
        context.setVariable("postalAddress", getValue(variables,"postal_address"));

        // ===============================
        // 💰 PRODUCT
        // ===============================
        context.setVariable("productName", getValue(variables,"product_name"));
        context.setVariable("productType", getValue(variables,"product_type"));
        context.setVariable("assetDescription", getValue(variables,"asset_description"));
        context.setVariable("tenure", getValue(variables,"financing_tenure"));
        context.setVariable("paymentStructure", getValue(variables,"payment_structure"));
        context.setVariable("financingAmount", getValue(variables,"cost_price"));
        context.setVariable("costPrice", getValue(variables,"cost_price"));
        context.setVariable("profitRate", getValue(variables,"profit_rate"));
        context.setVariable("profitAmount", getValue(variables,"profit_amount"));
        context.setVariable("totalSalePrice", getValue(variables,"total_sale_price"));
        context.setVariable("monthlyInstallment", getValue(variables,"monthly_installment"));

        // ===============================
        // 🏢 BUSINESS
        // ===============================
        context.setVariable("customerCategory", getValue(variables,"customer_category"));
        context.setVariable("businessSector", getValue(variables,"business_sector"));
        context.setVariable("monthlyIncome", getValue(variables,"monthly_net_income"));
        context.setVariable("businessRevenue", getValue(variables,"monthly_business_revenue"));
        context.setVariable("annualTurnover", getValue(variables,"annual_turnover"));
        context.setVariable("yearsOfBusiness", getValue(variables,"years_of_business"));
        context.setVariable("existingObligations", getValue(variables,"existing_monthly_obligations"));
        context.setVariable("existingFacilities", getValue(variables,"number_of_existing_facilities"));
        context.setVariable("proposedInstallment", getValue(variables,"proposed_instalment"));
        context.setVariable("afterFacility", getValue(variables,"after_this_facility"));

        // ===============================
        // 🔐 SECURITY
        // ===============================
        context.setVariable("collateralType", getValue(variables,"security_type"));
        context.setVariable("securityDescription", getValue(variables,"security_description"));
        context.setVariable("securityOwnership", getValue(variables,"security_ownership"));

        // ===============================
        // 🏦 BANK
        // ===============================
        context.setVariable("bankHolderName", getValue(variables,"bank_holder_name"));
        context.setVariable("accountNumber", getValue(variables,"account_number"));
        context.setVariable("accountType", getValue(variables,"account_type"));

        // ===============================
        // 🚗 / 🏠 ASSET DETAILS
        // ===============================
        context.setVariable("vehicleRegistration", getValue(variables,"vehicle_registration"));
        context.setVariable("chassisNumber", getValue(variables,"chassis_number"));
        context.setVariable("titleDeedNumber", getValue(variables,"title_deed_number"));
        context.setVariable("propertyLocation", getValue(variables,"property_location"));

        context.setVariable("valuerName", getValue(variables,"valuer_name"));
        context.setVariable("valuationDate", getValue(variables,"valuation_date"));
        context.setVariable("marketValue", getValue(variables,"estimated_market_value"));
        context.setVariable("forcedSaleValue", getValue(variables,"forced_sale_value"));

        // ===============================
        // 👥 GUARANTOR
        // ===============================
        context.setVariable("guarantorCustomerId", getValue(variables,"guarantor_customer_id"));
        context.setVariable("guarantorName", getValue(variables,"guarantor_full_name"));
        context.setVariable("guarantorNationalId", getValue(variables,"guarantor_national_id"));
        context.setVariable("guarantorAddress", getValue(variables,"guarantor_address"));
        context.setVariable("guarantorMobile", getValue(variables,"guarantor_mobile"));
        context.setVariable("guarantorEmail", getValue(variables,"guarantor_email"));
        context.setVariable("guarantorStatus", getValue(variables,"guarantor_status"));

        context.setVariable("employer", getValue(variables,"employer_name"));
        context.setVariable("grossIncome", getValue(variables,"guarantor_gross_income"));
        context.setVariable("netIncome", getValue(variables,"guarantor_net_income"));
        context.setVariable("obligations", getValue(variables,"guarantor_obligations"));

        context.setVariable("processInstanceId", processInstanceId);
        context.setVariable("completionTime", java.time.LocalDateTime.now());

        // ===============================
        // 📧 SUBJECT
        // ===============================
        context.setVariable(
                "viewLink",
                "http://localhost:5173/applications/" + processInstanceId
        );
        String subject = "Your loan application — DIB Bank Kenya — Reference "
                + getValue(variables, "referenceId");
        // ===============================
        // 📩 SEND EMAIL TO REQUESTER and CUSTOMER
        // ===============================
        mailNotificationUtil.sendEmail(
                getValue(variables, "email_id"),
                subject,
                "email/islamic-financial-decline-notification.html",
                context
        );

        if (requesterEmailId != null && !requesterEmailId.isEmpty()) {
            mailNotificationUtil.sendEmail(
                    requesterEmailId,
                    subject,
                    "email/islamic-financial-decline-notification.html",
                    context
            );
            log.info("Initiation notification sent to REQUESTER {}", requesterEmailId);
        }

        if (adminEmail != null && !adminEmail.isEmpty() && notificationEnabled) {
            mailNotificationUtil.sendEmail(
                    adminEmail,
                    subject,
                    "email/islamic-financial-decline-notification.html",
                    context
            );
            log.info("Initiation notification sent to REQUESTER {}", requesterEmailId);
        }


    }

    private String getValue(Map<String, Object> variables, String key) {
        return variables.get(key) == null ? "" : variables.get(key).toString();
    }

    private String getString(Object value) {

        return value == null ? null : value.toString();
    }

    private Long getLong(Object value) {

        if (value == null) {
            return null;
        }

        return Long.parseLong(value.toString());
    }

    private Integer getInteger(Object value) {

        if (value == null) {
            return null;
        }

        return Integer.parseInt(value.toString());
    }

    private Boolean getBoolean(Object value) {

        if (value == null) {
            return null;
        }

        return Boolean.parseBoolean(value.toString());
    }

    private BigDecimal getBigDecimal(Object value) {

        if (value == null) {
            return null;
        }

        return new BigDecimal(value.toString());
    }
}
