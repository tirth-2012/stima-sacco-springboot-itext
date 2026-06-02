package com.rutusoft.flowable.bpm.service.listeners;

import com.rutusoft.flowable.entity.ProcessInstanceAudit;
import com.rutusoft.flowable.entity.TaskActionLog;
import com.rutusoft.flowable.enums.ActionType;
import com.rutusoft.flowable.repository.ProcessInstanceAuditRepository;
import com.rutusoft.flowable.repository.TaskActionLogRepository;
import com.rutusoft.flowable.utility.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.idm.api.User;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("auditListenerService")
@Slf4j
public class AuditListenerService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskActionLogRepository taskActionLogRepository;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ProcessInstanceAuditRepository processInstanceAuditRepository;

    public void persistProcessAudit(DelegateExecution execution) {
        log.info("Creating audit for process instance : {}, event : {}",execution.getProcessInstanceId(), execution.getEventName());
        Boolean shouldLogEvent = false;
        ProcessInstanceAudit processInstanceAudit = new ProcessInstanceAudit();
        processInstanceAudit.setProcessInstanceId(execution.getProcessInstanceId());
        processInstanceAudit.setActionTime(new Date());
        if(execution.getEventName().equals("start")) {
            shouldLogEvent = true;
            String requester = execution.getVariable("requester") == null ? "": execution.getVariable("requester").toString();
            processInstanceAudit.setTitle("Application initiated");
            processInstanceAudit.setAction("Application initiated");
            processInstanceAudit.setActionedBy(requester);
        } else if(execution.getEventName().equals("end")){
            shouldLogEvent = true;
            processInstanceAudit.setTitle("Application closed ");
            processInstanceAudit.setAction("Application closed");
            if(execution.hasVariable("currentStage")) {
                execution.removeVariable("currentStage");
            }
        }
        if(shouldLogEvent = true) {
            processInstanceAuditRepository.save(processInstanceAudit);
            log.info("Persisted audit for process instance : {}", processInstanceAudit);
        }
        else {
            log.info("Skipping this event from process instance auditing : {}", processInstanceAudit);
        }
    }

    public void persistTaskAudit(DelegateTask task) {
        log.info("Creating audit for task instance : {}, task id : {}, event : {}",task.getProcessInstanceId(), task.getId(), task.getEventName());
        log.info("task name : {}, task assignee : {}", task.getName(), task.getAssignee());
        ProcessInstanceAudit processInstanceAudit = new ProcessInstanceAudit();
        processInstanceAudit.setProcessInstanceId(task.getProcessInstanceId());
        processInstanceAudit.setTaskId(task.getId());
        processInstanceAudit.setActionTime(new Date());
        Boolean shouldLogEvent = false;
        if(task.getEventName().equals("create")) {
            shouldLogEvent = true;
            processInstanceAudit.setTitle("Task "+task.getName()+" created");
            processInstanceAudit.setAction("Created");
        }
        else if(task.getEventName().equals("assignment")) {
            shouldLogEvent = true;
            if(task.getAssignee() != null) {
                processInstanceAudit.setTitle("Task " + task.getName() + " assigned");
                processInstanceAudit.setAction("Assigned");
                processInstanceAudit.setActionedBy(task.getAssignee());
            }
            else {
                processInstanceAudit.setTitle("Task " + task.getName() + " released");
                processInstanceAudit.setAction("Released");
                processInstanceAudit.setActionedBy(securityUtil.getCurrentUserId());
            }
        }
        else if(task.getEventName().equals("complete")) {
            shouldLogEvent = true;
            processInstanceAudit.setTitle("Task "+task.getName()+" completed");
            processInstanceAudit.setAction("Completed");
            processInstanceAudit.setActionedBy(task.getAssignee());

            String taskAction = task.getVariable("action") == null ? "" : task.getVariable("action").toString();
            String taskActionReason = task.getVariable("action_reason") == null ? "" : task.getVariable("action_reason").toString();
            String taskActionBy = task.getVariable("action_by") == null ? "" : task.getVariable("action_by").toString();
            String taskActionByFullname = "";
            if(!taskAction.equals("")){
                List<User> users = identityService.createUserQuery().userId(taskActionBy).list();
                if(!users.isEmpty()) {
                    taskActionByFullname = users.get(0).getFirstName() + " "+users.get(0).getLastName();
                }
            }
            persistTaskActionLog(task.getProcessInstanceId(), task.getId(), task.getName(), taskActionBy, taskActionByFullname, taskAction, taskActionReason);
        }
        else if(task.getEventName().equals("delete")) {
            shouldLogEvent = false;
        }

        if(shouldLogEvent) {
            processInstanceAuditRepository.save(processInstanceAudit);
            log.info("Persisted audit for task : {}", processInstanceAudit);
        } else {
            log.info("Skipping this event from task auditing : {}", processInstanceAudit);
        }
    }

    public void persistTaskActionLog(String processInstanceId, String taskId, String taskName, String actionBy, String taskActionByFullname, String actionName, String actionComment) {
        TaskActionLog taskActionLog = TaskActionLog.builder()
                .processInstanceId(processInstanceId)
                .taskId(taskId)
                .taskName(taskName)
                .action(ActionType.APPROVE)
                .actionBy(actionBy)
                .actionByName(taskActionByFullname)
                .comments(actionComment)
                .build();
        taskActionLogRepository.save(taskActionLog);
        log.info("Persisting task action log : {}", taskActionLog);
    }
}
