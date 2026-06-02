package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.ProcessDefinitionDto;

import java.util.List;

public interface ProcessDefinitionService {
    List<ProcessDefinitionDto> getLatestDefinitions();
    List<ProcessDefinitionDto> getActiveDefinitions();
    List<ProcessDefinitionDto> getDefinition(String processDefinitionId, int versionId);
}
