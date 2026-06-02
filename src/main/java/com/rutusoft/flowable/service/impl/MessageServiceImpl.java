package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.GuarantorResponseDto;
import com.rutusoft.flowable.service.GuarantorService;
import com.rutusoft.flowable.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final RuntimeService runtimeService;
    private final GuarantorService guarantorService;

    @Override
    public void triggerMessage(String processInstanceId, String messageName, Map<String, Object> messageData) {

        log.info("Triggering message event. processInstanceId={}, messageName={}", processInstanceId, messageName);

        try {

            // VALIDATIONS
            validateInputs(processInstanceId, messageName);

            // FIND SUBSCRIBED EXECUTIONS
            List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).messageEventSubscriptionName(messageName).list();

            // NO EXECUTION FOUND
            if (CollectionUtils.isEmpty(executions)) {

                log.warn("No execution found for processInstanceId={} and messageName={}", processInstanceId, messageName);

                return;
            }

            // MULTIPLE EXECUTIONS FOUND
            if (executions.size() > 1) {

                log.warn("Multiple executions found for processInstanceId={} and messageName={}. Using first execution.", processInstanceId, messageName);
            }

            Execution execution = executions.get(0);

            String executionId = execution.getId();

            log.info("Message subscription found. executionId={}", executionId);

            // SEND MESSAGE EVENT
            runtimeService.messageEventReceived(messageName, executionId, messageData != null ? messageData : Map.of());

            log.info("Successfully triggered message event. processInstanceId={}, executionId={}, messageName={}", processInstanceId, executionId, messageName);

        } catch (IllegalArgumentException ex) {
            log.error("Validation failed while triggering message event", ex);
            throw ex;

        } catch (Exception ex) {
            log.error("Unexpected error while triggering message event. processInstanceId={}, messageName={}", processInstanceId, messageName, ex);
            throw new RuntimeException("Failed to trigger Flowable message event", ex);
        }
    }

    private void validateInputs(String processInstanceId, String messageName) {

        if (!StringUtils.hasText(processInstanceId)) {

            throw new IllegalArgumentException("Process instance ID must not be null or empty");
        }

        if (!StringUtils.hasText(messageName)) {

            throw new IllegalArgumentException("Message name must not be null or empty");
        }
    }
}