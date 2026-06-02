package com.rutusoft.flowable.dto;

import lombok.Data;

@Data
public class VariableInstanceDto {
    private String id;
    private String processInstanceId;
    private String processDefinitionId;
    private String name;
    private String typeName;
    private Object value;
}
