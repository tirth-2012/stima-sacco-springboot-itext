package com.rutusoft.flowable.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TaskGroupDto {
    private String taskDefinitionKey;
    private String name;
    private int count;
    private List<ApplicationDto> applications;
}