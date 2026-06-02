package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.entity.TaskActionLog;
import com.rutusoft.flowable.service.TaskActionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task-action-logs")
@RequiredArgsConstructor
@Slf4j
public class TaskActionLogController {
    private final TaskActionLogService taskActionLogService;

    @GetMapping("/process-instances/{processInstanceId}")
    public ResponseEntity<List<TaskActionLog>> getTaskActionLogsByProcessInstanceId(
            @PathVariable("processInstanceId") String processInstanceId) {
        return ResponseEntity.ok(taskActionLogService.fetchTaskActionLogsByProcessInstanceId(processInstanceId));
    }
}