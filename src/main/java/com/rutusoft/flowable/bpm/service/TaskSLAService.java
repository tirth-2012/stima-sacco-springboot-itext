package com.rutusoft.flowable.bpm.service;

import lombok.extern.slf4j.Slf4j;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service("taskSLAService")
@Slf4j
public class TaskSLAService {
    public void setSLA(DelegateTask delegateTask, int hours, int mins){
        if(delegateTask.getEventName().equalsIgnoreCase("assignment")) {
            log.info("Setting SLA for task: {}, hours: {}, mins: {}",
                    delegateTask.getName(), hours, mins);

            // Calculate due date
            LocalDateTime dueDateTime = LocalDateTime.now()
                    .plusHours(hours)
                    .plusMinutes(mins);

            // Convert to java.util.Date
            Date dueDate = Date.from(
                    dueDateTime.atZone(ZoneId.systemDefault()).toInstant()
            );

            // Set due date
            delegateTask.setDueDate(dueDate);
        }
    }
}
