package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.GuarantorResponseDto;
import com.rutusoft.flowable.entity.Guarantor;
import com.rutusoft.flowable.enums.Status;
import com.rutusoft.flowable.repository.GuarantorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuarantorApprovalVerificationCheckerService {

    private final RuntimeService runtimeService;
    private final GuarantorService guarantorService;
    private final GuarantorRepository guarantorRepository;
    private final MessageService messageService;

    @Scheduled(fixedDelayString = "${mail-reader.poll-delay:30000}")
    public void checkGuarantorVerificationCompletion() {
        log.info("Checking if any guarantor verification completed for application...");

        List<Execution> executions = runtimeService
                .createExecutionQuery()
                .messageEventSubscriptionName("message_wait_guarantor_approval")
                .list();

        log.info("Execution size : {}", executions.size());
        if (executions.isEmpty()) {
            log.warn("Skipping guarantor verification because no executions are pending");
            return;
        }

        for (Execution execution : executions) {

            String processInstanceId = execution.getProcessInstanceId();

            Boolean isVerificationCompleted =
                    isGuarantorVerificationCompleted(processInstanceId);

            log.info("Guarantor verification completed : {}", isVerificationCompleted);

            if (Boolean.TRUE.equals(isVerificationCompleted)) {

                log.info("Triggering message for process instance : {}", processInstanceId);

                Boolean skipGuarantorVerification = shouldSkipGuarantorVerification(processInstanceId);

                Map<String, Object> messageData = new HashMap<>();
                messageData.put("skipGuarantorVerification", skipGuarantorVerification);

                messageService.triggerMessage(
                        processInstanceId,
                        "message_wait_guarantor_approval",
                        messageData
                );
            }
        }
    }

    private Boolean isGuarantorVerificationCompleted(String processInstanceId) {
        List<Guarantor> guarantors = guarantorRepository.findByProcessInstanceIdAndStatus(processInstanceId, Status.PENDING.getCode());
        log.info("Guarantor : {} with Pending status for process instance : {}", guarantors.size(), processInstanceId);
        return guarantors.isEmpty();
    }

    private Boolean shouldSkipGuarantorVerification(String processInstanceId) {

        List<Guarantor> pendingGuarantors =
                guarantorRepository.findByProcessInstanceIdAndStatus(
                        processInstanceId,
                        Status.PENDING.getCode()
                );

        if (!pendingGuarantors.isEmpty()) {
            return false;
        }

        List<Guarantor> declinedGuarantors =
                guarantorRepository.findByProcessInstanceIdAndStatus(
                        processInstanceId,
                        Status.REJECTED.getCode()
                );

        List<Guarantor> approvedGuarantors =
                guarantorRepository.findByProcessInstanceIdAndStatus(
                        processInstanceId,
                        Status.APPROVED.getCode()
                );

        return declinedGuarantors.isEmpty();
    }

}