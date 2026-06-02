package com.rutusoft.flowable.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ProcessInstanceDto {
    private String processInstanceId;
    private String processDefinitionId;
    private String processDefinitionName;
    private String processDefinitionKey;
    private int version;
    private String deploymentId;
    private String businessKey;
    private String startUserId;
    private Date startTime;
    private Date endTime;
    private boolean isEnded;
    private String deleteReason;
    private boolean isSuspended;
    private String status;
    private List<VariableInstanceDto> processVariables;
}
