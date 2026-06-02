package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.LoanApplicationRequestDto;
import com.rutusoft.flowable.dto.LoanApplicationResponseDto;
import com.rutusoft.flowable.entity.LoanApplication;
import com.rutusoft.flowable.enums.Status;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.repository.LoanApplicationRepository;
import com.rutusoft.flowable.service.LoanApplicationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanApplicationServiceImpl implements LoanApplicationService {
    private final LoanApplicationRepository repository;

    // ------------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------------
    @Override
    public LoanApplicationResponseDto createApplication(
            LoanApplicationRequestDto dto
    ) {

        log.info("Creating loan application");

        try {

            if (dto.getReferenceId() != null &&
                    repository.findByReferenceId(dto.getReferenceId()).isPresent()) {

                throw new ValidationException(
                        "Reference ID already exists"
                );
            }

            LoanApplication application = mapToEntity(dto);

            LoanApplication saved = repository.save(application);

            log.info("Loan application created successfully");

            return mapToResponse(saved);

        } catch (ValidationException e) {

            log.error("Validation error: {}", e.getMessage());
            throw e;

        } catch (Exception e) {

            log.error("Unexpected error while creating application", e);
            throw new RuntimeException(
                    "Internal server error",
                    e
            );
        }
    }

    // ------------------------------------------------------------------------
    // GET ALL
    // ------------------------------------------------------------------------
    @Override
    public Page<LoanApplicationResponseDto> getAllApplications(
            int page,
            int size
    ) {

        log.info("Fetching all applications");

        Pageable pageable = PageRequest.of(page, size);

        return repository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // ------------------------------------------------------------------------
    // GET BY ID
    // ------------------------------------------------------------------------
    @Override
    public LoanApplicationResponseDto getApplicationById(Long id) {

        log.info("Fetching application with ID: {}", id);

        LoanApplication application = repository.findById(id)
                .orElseThrow(() ->
                        new ValidationException("Application not found")
                );

        return mapToResponse(application);
    }

    // ------------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------------
    @Override
    public LoanApplicationResponseDto updateApplication(
            Long id,
            LoanApplicationRequestDto dto
    ) {

        log.info("Updating application with ID: {}", id);

        try {

            LoanApplication application = repository.findById(id)
                    .orElseThrow(() ->
                            new ValidationException("Application not found")
                    );

            application.setReferenceId(dto.getReferenceId());
            application.setBusinessKey(dto.getBusinessKey());
            application.setProcessInstanceId(dto.getProcessInstanceId());
            application.setProcessDefinitionId(dto.getProcessDefinitionId());
            application.setCustomerId(dto.getCustomerId());
            application.setProductId(dto.getProductId());
            application.setRequester(dto.getRequester());
            application.setRequesterFullName(dto.getRequesterFullName());
            application.setRmUser(dto.getRmUser());
            application.setApplicationByCustomer(dto.getApplicationByCustomer());
            application.setProductType(dto.getProductType());
            application.setProductName(dto.getProductName());
            application.setLoanPurposeDescription(dto.getLoanPurposeDescription());
            application.setAssetDescription(dto.getAssetDescription());
            application.setCostPrice(dto.getCostPrice());
            application.setProfitRate(dto.getProfitRate());
            application.setProfitAmount(dto.getProfitAmount());
            application.setTotalLoanAmount(dto.getTotalLoanAmount());
            application.setFinancingTenor(dto.getFinancingTenor());
            application.setPaymentStructure(dto.getPaymentStructure());
            application.setMonthlyInstallment(dto.getMonthlyInstallment());
            application.setProposedInstalment(dto.getProposedInstalment());
            application.setAfterThisFacility(dto.getAfterThisFacility());
            application.setDisbursementType(dto.getDisbursementType());
            application.setBankName(dto.getBankName());
            application.setBranchName(dto.getBranchName());
            application.setAccountNumber(dto.getAccountNumber());
            application.setAccountType(dto.getAccountType());
            application.setSwiftCode(dto.getSwiftCode());
            application.setCustomerCategory(dto.getCustomerCategory());
            application.setBusinessSector(dto.getBusinessSector());
            application.setCurrentStage(dto.getCurrentStage());
            application.setStatus(dto.getStatus());
            application.setStartTime(dto.getStartTime());
            application.setEndTime(dto.getEndTime());

            LoanApplication updated = repository.save(application);

            return mapToResponse(updated);

        } catch (ValidationException e) {

            log.error("Validation error: {}", e.getMessage());
            throw e;

        } catch (Exception e) {

            log.error("Unexpected error while updating", e);
            throw new RuntimeException(
                    "Internal server error",
                    e
            );
        }
    }

    // ------------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------------
    @Override
    public void deleteApplication(Long id) {

        log.info("Deleting application with ID: {}", id);

        try {

            LoanApplication application = repository.findById(id)
                    .orElseThrow(() ->
                            new ValidationException("Application not found")
                    );

            repository.delete(application);

        } catch (DataIntegrityViolationException e) {

            log.error("Data integrity violation", e);

            throw new RuntimeException(
                    "Cannot delete application. Linked with other records."
            );

        } catch (Exception e) {

            log.error("Unexpected error while deleting", e);

            throw new RuntimeException(
                    "Internal server error",
                    e
            );
        }
    }

    // ------------------------------------------------------------------------
    // ENTITY MAPPER
    // ------------------------------------------------------------------------
    private LoanApplication mapToEntity(
            LoanApplicationRequestDto dto
    ) {

        LoanApplication application = new LoanApplication();

        application.setReferenceId(dto.getReferenceId());
        application.setBusinessKey(dto.getBusinessKey());
        application.setProcessInstanceId(dto.getProcessInstanceId());
        application.setProcessDefinitionId(dto.getProcessDefinitionId());
        application.setCustomerId(dto.getCustomerId());
        application.setProductId(dto.getProductId());
        application.setRequester(dto.getRequester());
        application.setRequesterFullName(dto.getRequesterFullName());
        application.setRmUser(dto.getRmUser());
        application.setApplicationByCustomer(dto.getApplicationByCustomer());
        application.setProductType(dto.getProductType());
        application.setProductName(dto.getProductName());
        application.setLoanPurposeDescription(dto.getLoanPurposeDescription());
        application.setAssetDescription(dto.getAssetDescription());
        application.setCostPrice(dto.getCostPrice());
        application.setProfitRate(dto.getProfitRate());
        application.setProfitAmount(dto.getProfitAmount());
        application.setTotalLoanAmount(dto.getTotalLoanAmount());
        application.setFinancingTenor(dto.getFinancingTenor());
        application.setPaymentStructure(dto.getPaymentStructure());
        application.setMonthlyInstallment(dto.getMonthlyInstallment());
        application.setProposedInstalment(dto.getProposedInstalment());
        application.setAfterThisFacility(dto.getAfterThisFacility());
        application.setDisbursementType(dto.getDisbursementType());
        application.setBankName(dto.getBankName());
        application.setBranchName(dto.getBranchName());
        application.setAccountNumber(dto.getAccountNumber());
        application.setAccountType(dto.getAccountType());
        application.setSwiftCode(dto.getSwiftCode());
        application.setCustomerCategory(dto.getCustomerCategory());
        application.setBusinessSector(dto.getBusinessSector());
        application.setCurrentStage(dto.getCurrentStage());
        application.setStatus(dto.getStatus());
        application.setStartTime(dto.getStartTime());
        application.setEndTime(dto.getEndTime());

        return application;
    }

    // ------------------------------------------------------------------------
    // RESPONSE MAPPER
    // ------------------------------------------------------------------------
    private LoanApplicationResponseDto mapToResponse(
            LoanApplication application
    ) {

        LoanApplicationResponseDto dto =
                new LoanApplicationResponseDto();

        dto.setId(application.getId());
        dto.setReferenceId(application.getReferenceId());
        dto.setBusinessKey(application.getBusinessKey());
        dto.setProcessInstanceId(application.getProcessInstanceId());
        dto.setProcessDefinitionId(application.getProcessDefinitionId());
        dto.setCustomerId(application.getCustomerId());
        dto.setProductId(application.getProductId());
        dto.setRequester(application.getRequester());
        dto.setRequesterFullName(application.getRequesterFullName());
        dto.setRmUser(application.getRmUser());
        dto.setApplicationByCustomer(application.getApplicationByCustomer());
        dto.setProductType(application.getProductType());
        dto.setProductName(application.getProductName());
        dto.setLoanPurposeDescription(application.getLoanPurposeDescription());
        dto.setAssetDescription(application.getAssetDescription());
        dto.setCostPrice(application.getCostPrice());
        dto.setProfitRate(application.getProfitRate());
        dto.setProfitAmount(application.getProfitAmount());
        dto.setTotalLoanAmount(application.getTotalLoanAmount());
        dto.setFinancingTenor(application.getFinancingTenor());
        dto.setPaymentStructure(application.getPaymentStructure());
        dto.setMonthlyInstallment(application.getMonthlyInstallment());
        dto.setProposedInstalment(application.getProposedInstalment());
        dto.setAfterThisFacility(application.getAfterThisFacility());
        dto.setDisbursementType(application.getDisbursementType());
        dto.setBankName(application.getBankName());
        dto.setBranchName(application.getBranchName());
        dto.setAccountNumber(application.getAccountNumber());
        dto.setAccountType(application.getAccountType());
        dto.setSwiftCode(application.getSwiftCode());
        dto.setCustomerCategory(application.getCustomerCategory());
        dto.setBusinessSector(application.getBusinessSector());
        dto.setCurrentStage(application.getCurrentStage());
        dto.setStatus(application.getStatus());
        dto.setStartTime(application.getStartTime());
        dto.setEndTime(application.getEndTime());
        dto.setCreatedAt(application.getCreatedAt());
        dto.setUpdatedAt(application.getUpdatedAt());

        return dto;
    }

    @Override
    @Transactional
    public void updateApplicationStatus(
            String processInstanceId,
            String status
    ) {

        log.info("================================================");
        log.info("Updating application status");
        log.info("ProcessInstanceId: {}", processInstanceId);
        log.info("New Status: {}", status);

        LoanApplication application =
                repository.findByProcessInstanceId(processInstanceId)
                        .orElseThrow(() -> {

                            log.error(
                                    "Loan application NOT FOUND for processInstanceId={}",
                                    processInstanceId
                            );

                            return new ValidationException(
                                    "Loan application not found"
                            );
                        });

        log.info(
                "Application found. ReferenceId={}, CurrentStatus={}",
                application.getReferenceId(),
                application.getStatus()
        );

        application.setStatus(status);

        repository.saveAndFlush(application);

        log.info(
                "Application status updated successfully to {}",
                status
        );

        log.info("================================================");
    }

    @Override
    public Long approvedLoanApplicationsCount() {
        return repository.countByStatus(Status.APPROVED.getCode());
    }

    @Override
    public Long approvedLoanApplicationsByUserCount(String userId) {
        return repository.countByRequesterAndStatus(userId, Status.APPROVED.getCode());
    }

    @Override
    public Long rejectedLoanApplicationsCount() {
        return repository.countByStatus(Status.DECLINED.getCode());
    }

    @Override
    public Long rejectedLoanApplicationsByUserCount(String userId) {
        return 0L;
    }

    @Override
    public Long activatedLoanApplicationsCount() {
        return 0L;
    }

    @Override
    public Long activatedLoanApplicationsByUserCount(String userId) {
        return 0L;
    }
}