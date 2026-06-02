package com.rutusoft.flowable.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ProcessDiagramDto {
    private List<TaskInstanceDto> allStages;
    private List<TaskInstanceDto> activeStages;
    private List<TaskInstanceDto> completedStages;
}
