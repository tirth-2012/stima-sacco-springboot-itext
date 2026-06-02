package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.*;
import com.rutusoft.flowable.enums.Status;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.service.LoanApplicationService;
import com.rutusoft.flowable.service.ProcessInstanceVariablesService;
import com.rutusoft.flowable.service.TaskInstanceService;
import com.rutusoft.flowable.utility.SecurityUtil;
import com.rutusoft.flowable.utility.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskInstanceServiceImpl implements TaskInstanceService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProcessInstanceVariablesService processInstanceVariablesService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public TaskInstancesResponseDto getAllActiveTasks(int from, int to) {
        TaskInstancesResponseDto taskInstancesResponseDto = new TaskInstancesResponseDto();
        List<TaskInstanceDto> taskInstanceDtos = new ArrayList<>();
        for(Task task : taskService.createTaskQuery().active().orderByTaskCreateTime().desc().listPage(from, to)) {
            TaskInstanceDto taskInstanceDto = new TaskInstanceDto();
            taskInstanceDto.setId(task.getId());
            taskInstanceDto.setName(task.getName());
            taskInstanceDto.setTaskDefinitionId(task.getTaskDefinitionId());
            taskInstanceDto.setTaskDefinitionKey(task.getTaskDefinitionKey());
            taskInstanceDto.setCategory(task.getCategory());
            taskInstanceDto.setDescription(task.getDescription());
            taskInstanceDto.setOwner(task.getOwner());
            taskInstanceDto.setPriority(task.getPriority());
            taskInstanceDto.setAssignee(fetchUserFullname(task.getAssignee()));
            taskInstanceDto.setTimeInQueue(TimeUtil.getTimeAgo(task.getCreateTime()));
            taskInstanceDto.setTimeSLARemaining(TimeUtil.getSLARemaining(task.getCreateTime(), task.getDueDate()));
            List<VariableInstanceDto> taskVariables = getTaskLocalVariables(task.getTaskLocalVariables());
            taskInstanceDto.setTaskLocalVariables(taskVariables);
            List<GroupDto> groups = new ArrayList<>();
            List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
            for(IdentityLink identityLink : identityLinks) {
                if(IdentityLinkType.CANDIDATE.equals(identityLink.getType())) {
                    String groupId = identityLink.getGroupId();
                    Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
                    GroupDto groupDto = new GroupDto();
                    groupDto.setGroupId(groupId);
                    groupDto.setName(group.getName());

                    groups.add(groupDto);
                }
            }

            taskInstanceDto.setGroups(groups);

            taskInstanceDto.setCreateTime(task.getCreateTime());
            taskInstanceDto.setClaimTime(task.getClaimTime());
            taskInstanceDto.setDueTime(task.getDueDate());
            taskInstanceDto.setTimeInQueue(TimeUtil.getTimeAgo(task.getCreateTime()));
            taskInstanceDto.setTimeSLARemaining(TimeUtil.getDifference(task.getClaimTime(), task.getDueDate()));
            taskInstanceDto.setStatus("In progress");
            taskInstanceDto.setProcessInstanceId(task.getProcessInstanceId());

            taskInstanceDto.setProcessVariables(processInstanceVariablesService.getProcessInstanceVariables(task.getProcessInstanceId()));
            taskInstanceDto.setTenantId(task.getTenantId());
            taskInstanceDtos.add(taskInstanceDto);
        }

        taskInstancesResponseDto.setTaskInstances(taskInstanceDtos);
        long count = taskService.createTaskQuery().active().count();
        taskInstancesResponseDto.setTotal(count);
        taskInstancesResponseDto.setTo(to);
        taskInstancesResponseDto.setFrom(from);
        return taskInstancesResponseDto;
    }

    @Override
    public TaskInstancesResponseDto getActiveTasksByAssignee(String assignee, int from, int to) {
        TaskInstancesResponseDto taskInstancesResponseDto = new TaskInstancesResponseDto();
        List<TaskInstanceDto> taskInstanceDtos = new ArrayList<>();
        for(Task task : taskService.createTaskQuery().active().taskAssignee(assignee).orderByTaskCreateTime().desc().listPage(from, to)) {
            TaskInstanceDto taskInstanceDto = new TaskInstanceDto();
            taskInstanceDto.setId(task.getId());
            taskInstanceDto.setName(task.getName());
            taskInstanceDto.setTaskDefinitionId(task.getTaskDefinitionId());
            taskInstanceDto.setTaskDefinitionKey(task.getTaskDefinitionKey());
            taskInstanceDto.setCategory(task.getCategory());
            taskInstanceDto.setDescription(task.getDescription());
            taskInstanceDto.setOwner(task.getOwner());
            taskInstanceDto.setPriority(task.getPriority());
            taskInstanceDto.setAssignee(fetchUserFullname(task.getAssignee()));

            List<GroupDto> groups = new ArrayList<>();
            List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
            for(IdentityLink identityLink : identityLinks) {
                if(IdentityLinkType.CANDIDATE.equals(identityLink.getType())) {
                    String groupId = identityLink.getGroupId();
                    Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
                    GroupDto groupDto = new GroupDto();
                    groupDto.setGroupId(groupId);
                    groupDto.setName(group.getName());

                    groups.add(groupDto);
                }
            }

            taskInstanceDto.setGroups(groups);

            taskInstanceDto.setCreateTime(task.getCreateTime());
            taskInstanceDto.setClaimTime(task.getClaimTime());
            taskInstanceDto.setDueTime(task.getDueDate());
            taskInstanceDto.setTimeInQueue(TimeUtil.getTimeAgo(task.getCreateTime()));
            taskInstanceDto.setTimeSLARemaining(TimeUtil.getSLARemaining(task.getCreateTime(), task.getDueDate()));
            taskInstanceDto.setStatus("In progress");
            taskInstanceDto.setProcessInstanceId(task.getProcessInstanceId());
            taskInstanceDto.setProcessVariables(processInstanceVariablesService.getProcessInstanceVariables(task.getProcessInstanceId()));
            //taskInstanceDto.setTaskLocalVariables(task.getTaskLocalVariables());
            taskInstanceDto.setTenantId(task.getTenantId());
            taskInstanceDtos.add(taskInstanceDto);
        }

        taskInstancesResponseDto.setTaskInstances(taskInstanceDtos);
        long count = taskService.createTaskQuery().active().taskAssignee(assignee).count();
        taskInstancesResponseDto.setTotal(count);
        taskInstancesResponseDto.setTo(to);
        taskInstancesResponseDto.setFrom(from);
        return taskInstancesResponseDto;
    }

    @Override
    public TaskInstancesResponseDto getActiveTasksByCandidateGroups(List<String> candidateGroups, int from, int to) {
        //List<String> candidateGroups = securityUtil.getCurrentUserGroups();
        log.info("candidateGroups : {}", candidateGroups);

        try {
            TaskInstancesResponseDto taskInstancesResponseDto = new TaskInstancesResponseDto();
            List<TaskInstanceDto> taskInstanceDtos = new ArrayList<>();
            for (Task task : taskService.createTaskQuery().taskCandidateGroupIn(candidateGroups).orderByTaskCreateTime().desc().listPage(from, to)) {
                TaskInstanceDto taskInstanceDto = new TaskInstanceDto();
                taskInstanceDto.setId(task.getId());
                taskInstanceDto.setName(task.getName());
                taskInstanceDto.setTaskDefinitionId(task.getTaskDefinitionId());
                taskInstanceDto.setTaskDefinitionKey(task.getTaskDefinitionKey());
                taskInstanceDto.setCategory(task.getCategory());
                taskInstanceDto.setDescription(task.getDescription());
                taskInstanceDto.setOwner(task.getOwner());
                taskInstanceDto.setPriority(task.getPriority());
                taskInstanceDto.setAssignee(fetchUserFullname(task.getAssignee()));
                taskInstanceDto.setTimeSLARemaining(TimeUtil.getSLARemaining(task.getCreateTime(), task.getDueDate()));
                List<GroupDto> groupDtos = new ArrayList<>();
                List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
                for (IdentityLink identityLink : identityLinks) {
                    if (IdentityLinkType.CANDIDATE.equals(identityLink.getType())) {
                        String groupId = identityLink.getGroupId();
                        Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
                        GroupDto groupDto = new GroupDto();
                        groupDto.setGroupId(groupId);
                        groupDto.setName(group.getName());

                        groupDtos.add(groupDto);
                    }
                }

                taskInstanceDto.setGroups(groupDtos);
                taskInstanceDto.setCreateTime(task.getCreateTime());
                taskInstanceDto.setClaimTime(task.getClaimTime());
                taskInstanceDto.setDueTime(task.getDueDate());
                taskInstanceDto.setStatus("Ready for Pickup");

                taskInstanceDto.setProcessInstanceId(task.getProcessInstanceId());
                taskInstanceDto.setProcessVariables(processInstanceVariablesService.getProcessInstanceVariables(task.getProcessInstanceId()));
                //taskInstanceDto.setTaskLocalVariables(task.getTaskLocalVariables());
                taskInstanceDto.setTenantId(task.getTenantId());
                taskInstanceDtos.add(taskInstanceDto);
            }

            taskInstancesResponseDto.setTaskInstances(taskInstanceDtos);
            long count = taskService.createTaskQuery().active().taskCandidateGroupIn(candidateGroups).count();
            taskInstancesResponseDto.setTotal(count);
            taskInstancesResponseDto.setTo(to);
            taskInstancesResponseDto.setFrom(from);
            return taskInstancesResponseDto;
        } catch (Exception e) {
            log.info("Error occurred while fetching active tasks by candidate group", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public TaskInstancesResponseDto getAllActiveTasksByProcessInstanceId(String processInstanceId, int from, int to) {
        TaskInstancesResponseDto taskInstancesResponseDto = new TaskInstancesResponseDto();
        List<TaskInstanceDto> taskInstanceDtos = new ArrayList<>();
        for(Task task : taskService.createTaskQuery().active().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().listPage(from, to)) {
            TaskInstanceDto taskInstanceDto = new TaskInstanceDto();
            taskInstanceDto.setId(task.getId());
            taskInstanceDto.setName(task.getName());
            taskInstanceDto.setTaskDefinitionId(task.getTaskDefinitionId());
            taskInstanceDto.setTaskDefinitionKey(task.getTaskDefinitionKey());
            taskInstanceDto.setCategory(task.getCategory());
            taskInstanceDto.setDescription(task.getDescription());
            taskInstanceDto.setOwner(task.getOwner());
            taskInstanceDto.setPriority(task.getPriority());
            taskInstanceDto.setAssignee(fetchUserFullname(task.getAssignee()));

            List<GroupDto> groups = new ArrayList<>();
            List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
            for(IdentityLink identityLink : identityLinks) {
                if(IdentityLinkType.CANDIDATE.equals(identityLink.getType())) {
                    String groupId = identityLink.getGroupId();
                    Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
                    GroupDto groupDto = new GroupDto();
                    groupDto.setGroupId(groupId);
                    groupDto.setName(group.getName());
                    groups.add(groupDto);
                }
            }

            taskInstanceDto.setGroups(groups);

            taskInstanceDto.setCreateTime(task.getCreateTime());
            taskInstanceDto.setClaimTime(task.getClaimTime());
            taskInstanceDto.setDueTime(task.getDueDate());
            taskInstanceDto.setTimeInQueue(TimeUtil.getTimeAgo(task.getCreateTime()));
            taskInstanceDto.setTimeSLARemaining(TimeUtil.getSLARemaining(task.getCreateTime(), task.getDueDate()));
            if(task.getAssignee() != null) {
                taskInstanceDto.setStatus("In progress");
            }
            else {
                taskInstanceDto.setStatus("Ready for Pickup");
            }
            taskInstanceDto.setProcessInstanceId(task.getProcessInstanceId());
            taskInstanceDto.setProcessVariables(processInstanceVariablesService.getProcessInstanceVariables(task.getProcessInstanceId()));
            //taskInstanceDto.setTaskLocalVariables(task.getTaskLocalVariables());
            taskInstanceDto.setTenantId(task.getTenantId());
            taskInstanceDtos.add(taskInstanceDto);
        }

        taskInstancesResponseDto.setTaskInstances(taskInstanceDtos);
        long count = taskService.createTaskQuery().active().processInstanceId(processInstanceId).count();
        taskInstancesResponseDto.setTotal(count);
        taskInstancesResponseDto.setTo(to);
        taskInstancesResponseDto.setFrom(from);
        return taskInstancesResponseDto;
    }

    @Override
    public TaskInstanceDto getTaskById(String taskId) {
        TaskInstanceDto taskInstanceDto = new TaskInstanceDto();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task != null) {
            taskInstanceDto.setId(task.getId());
            taskInstanceDto.setName(task.getName());
            taskInstanceDto.setTaskDefinitionId(task.getTaskDefinitionId());
            taskInstanceDto.setTaskDefinitionKey(task.getTaskDefinitionKey());
            taskInstanceDto.setCategory(task.getCategory());
            taskInstanceDto.setDescription(task.getDescription());
            taskInstanceDto.setOwner(task.getOwner());
            taskInstanceDto.setPriority(task.getPriority());
            taskInstanceDto.setAssignee(fetchUserFullname(task.getAssignee()));
            taskInstanceDto.setCreateTime(task.getCreateTime());
            taskInstanceDto.setClaimTime(task.getClaimTime());
            taskInstanceDto.setDueTime(task.getDueDate());
            taskInstanceDto.setTimeInQueue(TimeUtil.getTimeAgo(task.getCreateTime()));
            taskInstanceDto.setTimeSLARemaining(TimeUtil.getSLARemaining(task.getCreateTime(), task.getDueDate()));

            if(task.getAssignee() != null) {
                taskInstanceDto.setStatus("In progress");
            }
            else {
                taskInstanceDto.setStatus("Ready for Pickup");
            }
            taskInstanceDto.setProcessInstanceId(task.getProcessInstanceId());
            taskInstanceDto.setProcessVariables(processInstanceVariablesService.getProcessInstanceVariables(task.getProcessInstanceId()));
            //taskInstanceDto.setTaskLocalVariables(task.getTaskLocalVariables());
            taskInstanceDto.setTenantId(task.getTenantId());
        }
        return taskInstanceDto;
    }

    @Override
    public String claimTask(String taskId, String assignee) {
        //String currentUser = securityUtil.getCurrentUserId();
        List<Task> tasks = taskService.createTaskQuery().taskId(taskId).taskAssigned().list();
        if (!tasks.isEmpty()) {
            throw new ValidationException("Task is already claimed");
        }
        taskService.claim(taskId, assignee);
        return "Task claimed successfully";
    }

    @Override
    public String unClaimTask(String taskId) {
        log.info("Unclaiming task : {}", taskId);
        try {
            taskService.unclaim(taskId);
        } catch (Exception ex) {
            log.error("Task could not be unclaimed, Error : {}", ex.getMessage());
        }
        return "Task unclaimed successfully";
    }

    @Override
    public String saveTask(TaskInstanceUpdateDto instanceUpdateDto) {
        List<Task> tasks = taskService.createTaskQuery().taskId(instanceUpdateDto.getId()).active().list();
        if(!tasks.isEmpty()) {
            Task task = tasks.get(0);
            task.setAssignee(instanceUpdateDto.getAssignee());
            task.setName(instanceUpdateDto.getName());
            task.setDescription(instanceUpdateDto.getDescription());
            task.setCategory(instanceUpdateDto.getCategory());
            task.setDueDate(instanceUpdateDto.getDueTime());
            task.setPriority(instanceUpdateDto.getPriority());
            taskService.saveTask(task);
        }
        return "User task saved successfully";
    }

    @Override
    public String completeTask(String taskId, Map<String, Object> variables) {
        List<Task> tasks = taskService.createTaskQuery().taskId(taskId).active().list();
        if(tasks.isEmpty()) {
            throw new ValidationException("Task is already completed");
        }
        try {
            String taskDefinitionId = tasks.get(0).getTaskDefinitionKey();
            String processInstanceId = tasks.get(0).getProcessInstanceId();

            Map<String, Object> processInstanceVariables = new HashMap<>();
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                processInstanceVariables.put(taskDefinitionId+"_"+entry.getKey(), entry.getValue());
            }

            //Persisting user input so that can be populated for up-coming task in flow
            runtimeService.setVariables(processInstanceId, processInstanceVariables);

            taskService.complete(taskId, variables, true);

            // ----------------------------------------------------
            // CHECK IF PROCESS COMPLETED
            // ----------------------------------------------------
//            HistoricProcessInstance historicProcessInstance =
//                    historyService
//                            .createHistoricProcessInstanceQuery()
//                            .processInstanceId(processInstanceId)
//                            .finished()
//                            .singleResult();
//
//            if (historicProcessInstance != null) {
//
//                loanApplicationService.updateApplicationStatus(
//                        processInstanceId,
//                        Status.COMPLETED.getCode()
//                );
//
//                log.info(
//                        "Loan application marked as COMPLETED for processInstanceId: {}",
//                        processInstanceId
//                );
//            }
        } catch (Exception ex) {
            log.error("Task {} completion failed, Error : {}", taskId, ex.getMessage());
            throw new RuntimeException("Task completion failed");
        }
        return "Task completed successfully";
    }

    @Override
    public String addCandidateUser(String taskId, String candidateUser) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task != null) {
            taskService.addCandidateUser(taskId, candidateUser);
            return "Candidate user added successfully";
        }
        return "Candidate user could not be added as task is not exist with task id "+taskId;
    }

    @Override
    public String addCandidateGroup(String taskId, String candidateGroup) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Boolean isGroupExistOnTask = false;
        if(task != null) {
            taskService.addCandidateGroup(taskId, candidateGroup);
            return "Candidate group added successfully";
        }
        return "Candidate group could not be added as task is not exist with task id "+taskId;
    }

    @Override
    public String changeAssignee(String taskId, String assignee) {
        taskService.setAssignee(taskId, assignee);
        return "Task assignee changed successfully";
    }

    private List<VariableInstanceDto> getTaskLocalVariables(Map<String, Object> taskLocalVariables) {
        List<VariableInstanceDto> variableInstanceDtos = new ArrayList<>();

        if (taskLocalVariables == null || taskLocalVariables.isEmpty()) {
            return variableInstanceDtos;
        }

        for (Map.Entry<String, Object> entry : taskLocalVariables.entrySet()) {
            VariableInstanceDto dto = new VariableInstanceDto();
            dto.setName(entry.getKey());
            dto.setValue(entry.getValue());
            variableInstanceDtos.add(dto);
        }

        return variableInstanceDtos;
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
}