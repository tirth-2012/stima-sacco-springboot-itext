package com.rutusoft.flowable.bpm.service.listeners;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("workflowStageUpdateListener")
@Slf4j
public class WorkflowStageUpdateListener {
    public void updateCurrentStage(DelegateExecution execution) {
        execution.setVariable("currentStage", execution.getCurrentFlowElement().getName());
    }
}
