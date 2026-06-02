package com.rutusoft.flowable.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProcessInstancesResponseDto {
    List<ProcessInstanceDto> processInstances;
    int from;
    int to;
    long total;
}
