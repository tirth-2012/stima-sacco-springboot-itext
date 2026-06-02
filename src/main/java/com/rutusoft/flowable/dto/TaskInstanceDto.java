package com.rutusoft.flowable.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class TaskInstanceDto {
    private String id;
    private String name;
    private String taskDefinitionId;
    private String taskDefinitionKey;
    private String category;
    private String description;
    private String owner;
    private int priority;
    private String assignee;
    private List<GroupDto> groups;
    private Date createTime;
    private Date claimTime;
    private Date dueTime;
    private Date endTime;
    private String processInstanceId;
    private String tenantId;
    private String status;
    private String timeInQueue;
    private String timeSLARemaining;
    private List<VariableInstanceDto> processVariables;
    private List<VariableInstanceDto> taskLocalVariables;
}
