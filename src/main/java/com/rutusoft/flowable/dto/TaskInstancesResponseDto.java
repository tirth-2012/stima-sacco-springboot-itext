package com.rutusoft.flowable.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskInstancesResponseDto {
    List<TaskInstanceDto> taskInstances;
    int from;
    int to;
    long total;
}
