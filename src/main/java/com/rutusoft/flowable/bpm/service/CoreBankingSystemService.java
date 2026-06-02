package com.rutusoft.flowable.bpm.service;

import com.rutusoft.flowable.enums.Status;
import com.rutusoft.flowable.service.LoanApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

@Service("coreBankingSystemService")
@Slf4j
@RequiredArgsConstructor
public class CoreBankingSystemService {
    private final LoanApplicationService loanApplicationService;
    public void handOff(DelegateExecution execution) {
        log.info("CoreBankingSystemService handOff executing");
        String processInstanceId = execution.getProcessInstanceId();
        loanApplicationService.updateApplicationStatus(
                processInstanceId,
                Status.APPROVED.getCode()
        );
    }
}
