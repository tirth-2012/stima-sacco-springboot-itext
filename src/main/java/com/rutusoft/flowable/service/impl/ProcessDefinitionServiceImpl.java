package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.ProcessDefinitionDto;
import com.rutusoft.flowable.service.ProcessDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProcessDefinitionServiceImpl implements ProcessDefinitionService {

    @Autowired
    private RepositoryService repositoryService;

    @Override
    public List<ProcessDefinitionDto> getLatestDefinitions() {
        List<ProcessDefinitionDto> processDefinitionDtos = new ArrayList<>();

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .list();

        for(ProcessDefinition processDefinition : processDefinitions) {
            ProcessDefinitionDto processDefinitionDto = getProcessDefinitionDto(processDefinition);
            processDefinitionDtos.add(processDefinitionDto);
        }

        return processDefinitionDtos;
    }

    @Override
    public List<ProcessDefinitionDto> getActiveDefinitions() {
        List<ProcessDefinitionDto> processDefinitionDtos = new ArrayList<>();

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .active()
                .list();

        for(ProcessDefinition processDefinition : processDefinitions) {
            ProcessDefinitionDto processDefinitionDto = getProcessDefinitionDto(processDefinition);
            processDefinitionDtos.add(processDefinitionDto);
        }

        return processDefinitionDtos;
    }

    @Override
    public List<ProcessDefinitionDto> getDefinition(String processDefinitionId, int versionId) {
        List<ProcessDefinitionDto> processDefinitionDtos = new ArrayList<>();

        List<ProcessDefinition> processDefinitions =  repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .processDefinitionVersion(versionId)
                .list();

        for(ProcessDefinition processDefinition : processDefinitions) {
            ProcessDefinitionDto processDefinitionDto = getProcessDefinitionDto(processDefinition);

            processDefinitionDtos.add(processDefinitionDto);
        }
        return processDefinitionDtos;
    }

    private static ProcessDefinitionDto getProcessDefinitionDto(ProcessDefinition processDefinition) {
        log.info("processDefinition 1: {}", processDefinition);
        ProcessDefinitionDto processDefinitionDto = new ProcessDefinitionDto();
        processDefinitionDto.setId(processDefinition.getId());
        processDefinitionDto.setCategory(processDefinition.getCategory());
        processDefinitionDto.setName(processDefinition.getName());
        processDefinitionDto.setKey(processDefinition.getKey());
        processDefinitionDto.setDescription(processDefinition.getDescription());
        processDefinitionDto.setVersion(processDefinition.getVersion());
        processDefinitionDto.setDeploymentId(processDefinition.getDeploymentId());
        processDefinitionDto.setSuspended(processDefinition.isSuspended());
        processDefinitionDto.setDerivedFrom(processDefinition.getDerivedFrom());
        processDefinitionDto.setDerivedVersion(processDefinition.getDerivedFromRoot());
        processDefinitionDto.setDiagramResourceName(processDefinition.getDiagramResourceName());
        processDefinitionDto.setTenantId(processDefinition.getTenantId());
        return processDefinitionDto;
    }
}
