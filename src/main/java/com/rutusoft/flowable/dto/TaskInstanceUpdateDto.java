package com.rutusoft.flowable.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class TaskInstanceUpdateDto {
    private String id;
    private String name;
    private String taskDefinitionId;
    private String taskDefinitionKey;
    private String category;
    private String description;
    private String owner;
    private int priority;
    private String assignee;
    private Date createTime;
    private Date claimTime;
    private Date dueTime;
    private Date endTime;
    private String processInstanceId;
    private String tenantId;
    private Map<String, Object> processVariables;
    private Map<String, Object> taskLocalVariables;
}
