package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.LoanApplicationRequestDto;
import com.rutusoft.flowable.dto.LoanApplicationResponseDto;
import org.springframework.data.domain.Page;

public interface LoanApplicationService {

    LoanApplicationResponseDto createApplication(LoanApplicationRequestDto dto);
    Page<LoanApplicationResponseDto> getAllApplications(int page, int size);
    LoanApplicationResponseDto getApplicationById(Long id);
    LoanApplicationResponseDto updateApplication(Long id, LoanApplicationRequestDto dto);
    void deleteApplication(Long id);
    void updateApplicationStatus(String processInstanceId, String status);
    Long approvedLoanApplicationsCount();
    Long approvedLoanApplicationsByUserCount(String userId);
    Long rejectedLoanApplicationsCount();
    Long rejectedLoanApplicationsByUserCount(String userId);
    Long activatedLoanApplicationsCount();
    Long activatedLoanApplicationsByUserCount(String userId);
}