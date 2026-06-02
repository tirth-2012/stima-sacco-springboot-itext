package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.ProcessDiagramDto;
import com.rutusoft.flowable.dto.TaskInstanceDto;
import com.rutusoft.flowable.service.ProcessDiagramService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProcessDiagramServiceImpl implements ProcessDiagramService {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Override
    public ProcessDiagramDto processDiagram(String processInstanceId) {
        List<String> excludeTasks = new ArrayList<>();
        excludeTasks.add("Business Development Officer");
        excludeTasks.add("Branch Manager Supervision");
        excludeTasks.add("Shariah Officer Supervision");

        List<String> creditApprovalTasks = new ArrayList<>();

        creditApprovalTasks.add("Credit Officer");
        creditApprovalTasks.add("Senior Credit Manager");
        creditApprovalTasks.add("Credit Committee");
        creditApprovalTasks.add("Branch Credit Committee");

        excludeTasks.addAll(creditApprovalTasks);

        ProcessDiagramDto processDiagramDto = new ProcessDiagramDto();
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).list();
        if (!historicProcessInstances.isEmpty()) {
            String processDefinitionKey = historicProcessInstances.get(0).getProcessDefinitionKey();
            ProcessDefinition processDefinition =
                    repositoryService.createProcessDefinitionQuery()
                            .processDefinitionKey(processDefinitionKey)
                            .latestVersion()
                            .singleResult();

            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

            Process process = bpmnModel.getProcessById(processDefinitionKey);

            //Fetch all the tasks from process definition
            List<UserTask> allUserTasks = bpmnModel.getProcessById(processDefinitionKey).findFlowElementsOfType(UserTask.class);
            List<TaskInstanceDto> allStages = allUserTasks.stream()
                    .map(task -> {
                        String taskName = "";
                        if(creditApprovalTasks.contains(task.getName())) {
                            taskName = "Credit Approval";
                        }
                        else {
                            taskName = task.getName();
                        }
                        TaskInstanceDto dto = new TaskInstanceDto();
                        dto.setTaskDefinitionKey(task.getId());
                        dto.setName(task.getName());
                        return dto;
                    })
                    .collect(Collectors.toList());
            processDiagramDto.setAllStages(allStages);

            //Fetch active stages
            List<Task> activeTasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            List<TaskInstanceDto> activeStages = activeTasks.stream().map(task -> {
                String taskName = "";
                if(creditApprovalTasks.contains(task.getName())) {
                    taskName = "Credit Approval";
                }
                else {
                    taskName = task.getName();
                }
                TaskInstanceDto dto = new TaskInstanceDto();
                dto.setTaskDefinitionKey(task.getId());
                dto.setName(taskName);
                dto.setAssignee(task.getAssignee());
                dto.setCreateTime(task.getCreateTime());
                return dto;
            }).collect(Collectors.toList());

            processDiagramDto.setActiveStages(activeStages);

            //Fetch completed stages
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).finished().list();
            List<TaskInstanceDto> completeStages = historicTaskInstances.stream().map(task -> {
                TaskInstanceDto dto = new TaskInstanceDto();
                dto.setTaskDefinitionKey(task.getId());
                dto.setName(task.getName());
                dto.setAssignee(task.getAssignee());
                dto.setCreateTime(task.getCreateTime());
                dto.setEndTime(task.getEndTime());
                return dto;
            }).collect(Collectors.toList());

            processDiagramDto.setCompletedStages(completeStages);
        }

        //log.info("processDiagramDto : {}", processDiagramDto);
        return processDiagramDto;
    }
}
