package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.ProcessDiagramDto;

import java.util.List;

public interface ProcessDiagramService {
    ProcessDiagramDto processDiagram(String processInstanceId);
}
