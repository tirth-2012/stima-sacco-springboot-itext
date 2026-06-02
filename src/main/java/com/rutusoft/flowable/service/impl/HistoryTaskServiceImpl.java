package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.GroupDto;
import com.rutusoft.flowable.dto.TaskInstanceDto;
import com.rutusoft.flowable.dto.TaskInstancesResponseDto;
import com.rutusoft.flowable.dto.VariableInstanceDto;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.service.HistoryTaskService;
import com.rutusoft.flowable.service.ProcessInstanceVariablesService;
import com.rutusoft.flowable.utility.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class HistoryTaskServiceImpl implements HistoryTaskService {
    @Autowired
    private HistoryService historyService;

    @Autowired
    private ProcessInstanceVariablesService processInstanceVariablesService;

    @Autowired
    private IdentityService identityService;

    @Override
    public TaskInstancesResponseDto getCompletedTaskByUserId(String userId, int from, int to) {
        List<TaskInstanceDto> taskInstanceDtos = new ArrayList<>();
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished().orderByHistoricTaskInstanceEndTime().desc().listPage(from, to);
        long count = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished().count();
        for(HistoricTaskInstance task : historicTaskInstances) {
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
            List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForTask(task.getId());
            for(HistoricIdentityLink identityLink : identityLinks) {
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
            taskInstanceDto.setEndTime(task.getEndTime());
            taskInstanceDto.setTimeInQueue(TimeUtil.getDifference(task.getCreateTime(), task.getEndTime()));
            taskInstanceDto.setStatus("Completed");
            taskInstanceDto.setProcessInstanceId(task.getProcessInstanceId());

            List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId()).list();
            List<VariableInstanceDto> variableInstanceDtos = new ArrayList<>();

            for(HistoricVariableInstance variableInstance : historicVariableInstances) {
                VariableInstanceDto variableInstanceDto = new VariableInstanceDto();
                variableInstanceDto.setId(variableInstance.getId());
                variableInstanceDto.setName(variableInstance.getVariableName());
                variableInstanceDto.setProcessInstanceId(variableInstance.getProcessInstanceId());
                variableInstanceDto.setTypeName(variableInstance.getVariableTypeName());
                variableInstanceDto.setValue(variableInstance.getValue());
                variableInstanceDtos.add(variableInstanceDto);
            }

            List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery().taskId(task.getId()).list();

            List<VariableInstanceDto> taskLocalVariables = getTaskLocalVariables(variableInstances);
            taskInstanceDto.setTaskLocalVariables(taskLocalVariables);
            taskInstanceDto.setProcessVariables(variableInstanceDtos);
            taskInstanceDto.setTenantId(task.getTenantId());
            taskInstanceDtos.add(taskInstanceDto);
        }

        TaskInstancesResponseDto taskInstancesResponseDto = new TaskInstancesResponseDto();
        taskInstancesResponseDto.setTaskInstances(taskInstanceDtos);
        taskInstancesResponseDto.setFrom(from);
        taskInstancesResponseDto.setTo(to);
        taskInstancesResponseDto.setTotal(count);

        return taskInstancesResponseDto;
    }

    @Override
    public TaskInstancesResponseDto getAllTasks(String assignee,
                                                List<String> candidateGroups,
                                                int from,
                                                int size) {

        log.info("Fetching tasks for assignee: {}, groups: {}", assignee, candidateGroups);

        List<TaskInstanceDto> taskInstanceDtos = new ArrayList<>();

        try {
            // =========================
            // 1. Assigned Tasks (Active + Completed)
            // =========================
            List<HistoricTaskInstance> assignedTasks =
                    historyService.createHistoricTaskInstanceQuery()
                            .taskAssignee(assignee)
                            .orderByHistoricTaskInstanceStartTime()
                            .desc()
                            .list();

            // =========================
            // 2. Candidate Group Tasks
            // =========================
            List<HistoricTaskInstance> groupTasks = new ArrayList<>();

            if (candidateGroups != null && !candidateGroups.isEmpty()) {
                groupTasks = historyService.createHistoricTaskInstanceQuery()
                        .taskCandidateGroupIn(candidateGroups)
                        .taskUnassigned() // only available tasks
                        .orderByHistoricTaskInstanceStartTime()
                        .desc()
                        .list();
            }

            // =========================
            // 3. Merge + Remove duplicates
            // =========================
            Map<String, HistoricTaskInstance> uniqueTasks = new LinkedHashMap<>();

            for (HistoricTaskInstance task : assignedTasks) {
                uniqueTasks.put(task.getId(), task);
            }

            for (HistoricTaskInstance task : groupTasks) {
                uniqueTasks.putIfAbsent(task.getId(), task);
            }

            List<HistoricTaskInstance> allTasks =
                    new ArrayList<>(uniqueTasks.values());

            // =========================
            // 4. Pagination (manual)
            // =========================
            int total = allTasks.size();
            int toIndex = Math.min(from + size, total);

            List<HistoricTaskInstance> paginatedTasks =
                    (from > total) ? Collections.emptyList() : allTasks.subList(from, toIndex);

            // =========================
            // 5. Mapping
            // =========================
            for (HistoricTaskInstance task : paginatedTasks) {

                TaskInstanceDto dto = new TaskInstanceDto();
                dto.setId(task.getId());
                dto.setName(task.getName());
                dto.setTaskDefinitionKey(task.getTaskDefinitionKey());
                dto.setAssignee(fetchUserFullname(task.getAssignee()));
                dto.setPriority(task.getPriority());
                dto.setCreateTime(task.getCreateTime());
                dto.setEndTime(task.getEndTime());
                dto.setProcessInstanceId(task.getProcessInstanceId());
                if(task.getEndTime() != null && task.getAssignee() != null) {
                    dto.setTimeInQueue(TimeUtil.getDifference(task.getCreateTime(), task.getEndTime()));
                }
                else if(task.getEndTime() != null && task.getAssignee() != null) {
                    dto.setTimeSLARemaining(TimeUtil.getSLARemaining(task.getCreateTime(), task.getDueDate()));
                    dto.setTimeInQueue(TimeUtil.getTimeAgo(task.getCreateTime()));
                } else {
                    dto.setTimeSLARemaining("-");
                    dto.setTimeInQueue(TimeUtil.getTimeAgo(task.getCreateTime()));

                }
                // ✅ Status
                dto.setStatus(task.getEndTime() != null ? "Completed" : "Active");

                // =========================
                // Groups
                // =========================
                List<GroupDto> groups = new ArrayList<>();
                List<HistoricIdentityLink> identityLinks =
                        historyService.getHistoricIdentityLinksForTask(task.getId());

                for (HistoricIdentityLink link : identityLinks) {
                    if (IdentityLinkType.CANDIDATE.equals(link.getType())) {
                        Group group = identityService.createGroupQuery()
                                .groupId(link.getGroupId())
                                .singleResult();

                        if (group != null) {
                            GroupDto g = new GroupDto();
                            g.setGroupId(group.getId());
                            g.setName(group.getName());
                            groups.add(g);
                        }
                    }
                }
                dto.setGroups(groups);

                // =========================
                // Variables (optional heavy ⚠️)
                // =========================
                List<HistoricVariableInstance> vars =
                        historyService.createHistoricVariableInstanceQuery()
                                .processInstanceId(task.getProcessInstanceId())
                                .list();

                List<VariableInstanceDto> varDtos = new ArrayList<>();
                for (HistoricVariableInstance var : vars) {
                    VariableInstanceDto v = new VariableInstanceDto();
                    v.setName(var.getVariableName());
                    v.setValue(var.getValue());
                    varDtos.add(v);
                }
                dto.setProcessVariables(varDtos);

                taskInstanceDtos.add(dto);
            }

            // =========================
            // Response
            // =========================
            TaskInstancesResponseDto response = new TaskInstancesResponseDto();
            response.setTaskInstances(taskInstanceDtos);
            response.setFrom(from);
            response.setTo(toIndex);
            response.setTotal(total);

            return response;

        } catch (Exception ex) {
            log.error("Error fetching tasks for user: {}", assignee, ex);
            throw new RuntimeException("Unable to fetch tasks");
        }
    }

    @Override
    public TaskInstancesResponseDto getTaskInstancesByProcessInstanceId(String processInstanceId, int from, int to) {
        TaskInstancesResponseDto taskInstancesResponseDto = new TaskInstancesResponseDto();
        List<TaskInstanceDto> taskInstanceDtos = new ArrayList<>();
        for(HistoricTaskInstance task : historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().listPage(from, to)) {
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
            List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForTask(task.getId());
            for(HistoricIdentityLink identityLink : identityLinks) {
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
            taskInstanceDto.setTimeInQueue(TimeUtil.getTimeAgo(task.getClaimTime()));
            taskInstanceDto.setTimeSLARemaining(TimeUtil.getDifference(task.getClaimTime(), task.getDueDate()));
            if(task.getAssignee() != null) {
                taskInstanceDto.setStatus("In progress");
            }
            else {
                taskInstanceDto.setStatus("Ready for Pickup");
            }
            taskInstanceDto.setProcessInstanceId(task.getProcessInstanceId());
            taskInstanceDto.setProcessVariables(processInstanceVariablesService.getProcessInstanceVariables(task.getProcessInstanceId()));
            List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery().taskId(task.getId()).list();
            taskInstanceDto.setTaskLocalVariables(getTaskLocalVariables(variableInstances));
            taskInstanceDto.setTenantId(task.getTenantId());
            taskInstanceDtos.add(taskInstanceDto);
        }

        taskInstancesResponseDto.setTaskInstances(taskInstanceDtos);
        long count = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).count();
        taskInstancesResponseDto.setTotal(count);
        taskInstancesResponseDto.setTo(to);
        taskInstancesResponseDto.setFrom(from);
        return taskInstancesResponseDto;
    }

    @Override
    public TaskInstanceDto getTaskById(String taskId) {
        try {
            HistoricTaskInstance task = historyService
                    .createHistoricTaskInstanceQuery()
                    .taskId(taskId)
                    .singleResult();

            if (task == null) {
                throw new ValidationException("Task not found for id: " + taskId);
            }

            TaskInstanceDto dto = new TaskInstanceDto();

            dto.setId(task.getId());
            dto.setName(task.getName());
            dto.setTaskDefinitionId(task.getTaskDefinitionId());
            dto.setTaskDefinitionKey(task.getTaskDefinitionKey());
            dto.setCategory(task.getCategory());
            dto.setDescription(task.getDescription());
            dto.setOwner(task.getOwner());
            dto.setPriority(task.getPriority());
            dto.setAssignee(fetchUserFullname(task.getAssignee()));

            // Groups
            List<GroupDto> groups = new ArrayList<>();
            List<HistoricIdentityLink> identityLinks =
                    historyService.getHistoricIdentityLinksForTask(task.getId());

            for (HistoricIdentityLink link : identityLinks) {
                if (IdentityLinkType.CANDIDATE.equals(link.getType())) {
                    String groupId = link.getGroupId();

                    Group group = identityService.createGroupQuery()
                            .groupId(groupId)
                            .singleResult();

                    GroupDto groupDto = new GroupDto();
                    groupDto.setGroupId(groupId);
                    groupDto.setName(group != null ? group.getName() : null);

                    groups.add(groupDto);
                }
            }

            dto.setGroups(groups);

            // Time handling
            Date referenceTime = task.getClaimTime() != null
                    ? task.getClaimTime()
                    : task.getCreateTime();

            dto.setCreateTime(task.getCreateTime());
            dto.setClaimTime(task.getClaimTime());
            dto.setDueTime(task.getDueDate());
            dto.setEndTime(task.getEndTime());
            dto.setTimeInQueue(TimeUtil.getTimeAgo(referenceTime));

            if (task.getDueDate() != null) {
                dto.setTimeSLARemaining(
                        TimeUtil.getDifference(referenceTime, task.getDueDate())
                );
            }

            // Status
            if (task.getEndTime() != null) {
                dto.setStatus("Completed");
            } else if (task.getAssignee() != null) {
                dto.setStatus("In Progress");
            } else {
                dto.setStatus("Ready for Pickup");
            }

            dto.setProcessInstanceId(task.getProcessInstanceId());
            dto.setProcessVariables(
                    processInstanceVariablesService.getProcessInstanceVariables(
                            task.getProcessInstanceId()
                    )
            );

            List<HistoricVariableInstance> vars =
                    historyService.createHistoricVariableInstanceQuery()
                            .taskId(taskId)
                            .list();

            dto.setTaskLocalVariables(getTaskLocalVariables(vars));
            dto.setTenantId(task.getTenantId());

            return dto;

        } catch (Exception e) {
            log.error("Could not fetch task detail for taskId: {}", taskId, e);
            throw e;
        }
    }

    @Override
    public TaskInstancesResponseDto getAllCompletedTasksByProcessInstanceId(
            String processInstanceId, int from, int to) {

        TaskInstancesResponseDto response = new TaskInstancesResponseDto();

        List<HistoricTaskInstance> tasks = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .orderByHistoricTaskInstanceEndTime().asc() // better for completed tasks
                .list();


        VariableInstanceDto variableInstanceDto = processInstanceVariablesService.getProcessInstanceVariable(processInstanceId, "product_type");
        String productType = variableInstanceDto.getValue().toString();
        String bdoTaskName = "";
        String bdoTaskKey = "";
        if(productType.equalsIgnoreCase("Islamic Financing Products")) {
            bdoTaskName = "Business Development Officer";
            bdoTaskKey = "usertask_business_development_officer";
        } else {
            bdoTaskName = "Member";
            bdoTaskKey = "usertask_member";
        }
        // 🔥 Use Map for uniqueness
        Map<String, TaskInstanceDto> uniqueTasks = new LinkedHashMap<>();
        Boolean isBDOTaskExist = true;
        for (HistoricTaskInstance task : tasks) {

            String key = task.getTaskDefinitionKey();
            if(!key.equalsIgnoreCase("usertask_member")) {
                isBDOTaskExist = false;

            }

            // keep first occurrence OR latest (see below)
            if (!uniqueTasks.containsKey(key)) {
                TaskInstanceDto dto = new TaskInstanceDto();
                dto.setId(task.getId());
                dto.setName(task.getName());
                dto.setTaskDefinitionKey(key);
                dto.setAssignee(fetchUserFullname(task.getAssignee()));

                uniqueTasks.put(key, dto);
            }
        }

        if(!isBDOTaskExist) {
            TaskInstanceDto dto = new TaskInstanceDto();
            dto.setId("");
            dto.setName(bdoTaskName);
            dto.setTaskDefinitionKey(bdoTaskKey);
            dto.setAssignee("");
            uniqueTasks.put(bdoTaskKey, dto);
        }

        if(uniqueTasks.isEmpty()) {
            TaskInstanceDto dto = new TaskInstanceDto();
            dto.setId("");
            dto.setName(bdoTaskName);
            dto.setTaskDefinitionKey(bdoTaskKey);
            dto.setAssignee("");
            uniqueTasks.put(bdoTaskKey, dto);
        }
        List<TaskInstanceDto> result = new ArrayList<>(uniqueTasks.values());

        response.setTaskInstances(result);
        response.setTotal(result.size());
        response.setFrom(from);
        response.setTo(to);

        return response;
    }

    private List<VariableInstanceDto> getTaskLocalVariables(List<HistoricVariableInstance> variableInstances) {
        //log.info("getTaskLocalVariables invoked, variableInstances : {}", variableInstances);
        List<VariableInstanceDto> variableInstanceDtos = new ArrayList<>();

        if (variableInstances == null || variableInstances.isEmpty()) {
            return variableInstanceDtos;
        }

        for (HistoricVariableInstance variableInstance : variableInstances) {
            VariableInstanceDto dto = new VariableInstanceDto();
            dto.setName(variableInstance.getVariableName());
            dto.setValue(variableInstance.getValue());
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
