package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.entity.TaskActionLog;
import com.rutusoft.flowable.repository.TaskActionLogRepository;
import com.rutusoft.flowable.service.TaskActionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskActionLogServiceImpl implements TaskActionLogService {

    private final TaskActionLogRepository taskActionLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TaskActionLog> fetchTaskActionLogsByProcessInstanceId(String processInstanceId) {

        log.info("Fetching TaskActionLogs for processInstanceId: {}", processInstanceId);

        // ✅ Validation
        if (!StringUtils.hasText(processInstanceId)) {
            log.warn("Invalid processInstanceId received: {}", processInstanceId);
            return Collections.emptyList();
        }

        try {
            // ✅ Fetch sorted logs (timeline ready)
            List<TaskActionLog> logs =
                    taskActionLogRepository.findByProcessInstanceIdOrderByActionTimeDesc(processInstanceId);
            log.info("Fetched {} TaskActionLogs for processInstanceId: {}", logs.size(), processInstanceId);

            return logs;

        } catch (Exception ex) {
            log.error("Error fetching TaskActionLogs for processInstanceId: {}", processInstanceId, ex);
            throw new RuntimeException("Unable to fetch task action logs");
        }
    }
}