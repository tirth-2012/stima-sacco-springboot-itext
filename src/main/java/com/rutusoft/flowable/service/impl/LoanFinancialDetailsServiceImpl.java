package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.LoanFinancialDetailsRequestDto;
import com.rutusoft.flowable.dto.LoanFinancialDetailsResponseDto;
import com.rutusoft.flowable.entity.LoanApplication;
import com.rutusoft.flowable.entity.LoanFinancialDetails;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.repository.LoanApplicationRepository;
import com.rutusoft.flowable.repository.LoanFinancialDetailsRepository;
import com.rutusoft.flowable.service.LoanFinancialDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanFinancialDetailsServiceImpl implements LoanFinancialDetailsService {
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanFinancialDetailsRepository repository;

    // ------------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------------
    @Override
    public LoanFinancialDetailsResponseDto createFinancialDetails(
            LoanFinancialDetailsRequestDto dto
    ) {

        log.info("Creating loan financial details");

        try {

            if (dto.getLoanApplicationId() == null) {
                throw new ValidationException(
                        "Loan Application ID is required"
                );
            }

            LoanFinancialDetails entity = mapToEntity(dto);

            LoanFinancialDetails saved = repository.save(entity);

            log.info("Financial details created successfully");

            return mapToResponse(saved);

        } catch (ValidationException e) {

            log.error("Validation error: {}", e.getMessage());
            throw e;

        } catch (Exception e) {

            log.error("Unexpected error while creating financial details", e);

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
    public Page<LoanFinancialDetailsResponseDto> getAllFinancialDetails(
            int page,
            int size
    ) {

        log.info("Fetching all financial details");

        Pageable pageable = PageRequest.of(page, size);

        return repository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // ------------------------------------------------------------------------
    // GET BY ID
    // ------------------------------------------------------------------------
    @Override
    public LoanFinancialDetailsResponseDto getFinancialDetailsById(
            Long id
    ) {

        log.info("Fetching financial details with ID: {}", id);

        LoanFinancialDetails entity = repository.findById(id)
                .orElseThrow(() ->
                        new ValidationException(
                                "Financial details not found"
                        )
                );

        return mapToResponse(entity);
    }

    // ------------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------------
    @Override
    public LoanFinancialDetailsResponseDto updateFinancialDetails(
            Long id,
            LoanFinancialDetailsRequestDto dto
    ) {

        log.info("Updating financial details with ID: {}", id);

        try {

            LoanFinancialDetails entity = repository.findById(id)
                    .orElseThrow(() ->
                            new ValidationException(
                                    "Financial details not found"
                            )
                    );

            LoanApplication loanApplication =
                    loanApplicationRepository
                            .findById(dto.getLoanApplicationId())
                            .orElseThrow(() ->
                                    new ValidationException(
                                            "Loan application not found"
                                    )
                            );

            entity.setLoanApplication(loanApplication);
            entity.setMonthlyNetIncome(dto.getMonthlyNetIncome());
            entity.setMonthlyBusinessRevenue(dto.getMonthlyBusinessRevenue());
            entity.setAnnualTurnover(dto.getAnnualTurnover());
            entity.setYearsOfBusiness(dto.getYearsOfBusiness());
            entity.setExistingMonthlyObligations(
                    dto.getExistingMonthlyObligations()
            );
            entity.setNumberOfExistingFacilities(
                    dto.getNumberOfExistingFacilities()
            );
            entity.setDebtServiceRatio(dto.getDebtServiceRatio());
            entity.setCoverageRatio(dto.getCoverageRatio());


            LoanFinancialDetails updated = repository.save(entity);

            log.info("Financial details updated successfully");

            return mapToResponse(updated);

        } catch (ValidationException e) {

            log.error("Validation error: {}", e.getMessage());
            throw e;

        } catch (Exception e) {

            log.error("Unexpected error while updating financial details", e);

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
    public void deleteFinancialDetails(Long id) {

        log.info("Deleting financial details with ID: {}", id);

        try {

            LoanFinancialDetails entity = repository.findById(id)
                    .orElseThrow(() ->
                            new ValidationException(
                                    "Financial details not found"
                            )
                    );

            repository.delete(entity);

        } catch (DataIntegrityViolationException e) {

            log.error("Data integrity violation", e);

            throw new RuntimeException(
                    "Cannot delete financial details. Linked with other records."
            );

        } catch (Exception e) {

            log.error("Unexpected error while deleting financial details", e);

            throw new RuntimeException(
                    "Internal server error",
                    e
            );
        }
    }

    // ------------------------------------------------------------------------
    // ENTITY MAPPER
    // ------------------------------------------------------------------------
    private LoanFinancialDetails mapToEntity(
            LoanFinancialDetailsRequestDto dto
    ) {

        LoanFinancialDetails entity = new LoanFinancialDetails();

        LoanApplication loanApplication =
                loanApplicationRepository
                        .findById(dto.getLoanApplicationId())
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Loan application not found"
                                )
                        );

        entity.setLoanApplication(loanApplication);
        entity.setMonthlyNetIncome(dto.getMonthlyNetIncome());
        entity.setMonthlyBusinessRevenue(dto.getMonthlyBusinessRevenue());
        entity.setAnnualTurnover(dto.getAnnualTurnover());
        entity.setYearsOfBusiness(dto.getYearsOfBusiness());
        entity.setExistingMonthlyObligations(
                dto.getExistingMonthlyObligations()
        );
        entity.setNumberOfExistingFacilities(
                dto.getNumberOfExistingFacilities()
        );
        entity.setDebtServiceRatio(dto.getDebtServiceRatio());
        entity.setCoverageRatio(dto.getCoverageRatio());

        return entity;
    }

    // ------------------------------------------------------------------------
    // RESPONSE MAPPER
    // ------------------------------------------------------------------------
    private LoanFinancialDetailsResponseDto mapToResponse(
            LoanFinancialDetails entity
    ) {

        LoanFinancialDetailsResponseDto dto =
                new LoanFinancialDetailsResponseDto();

        dto.setId(entity.getId());
        if (entity.getLoanApplication() != null) {

            dto.setLoanApplicationId(
                    entity.getLoanApplication().getId()
            );
        }
        dto.setMonthlyNetIncome(entity.getMonthlyNetIncome());
        dto.setMonthlyBusinessRevenue(
                entity.getMonthlyBusinessRevenue()
        );
        dto.setAnnualTurnover(entity.getAnnualTurnover());
        dto.setYearsOfBusiness(entity.getYearsOfBusiness());
        dto.setExistingMonthlyObligations(
                entity.getExistingMonthlyObligations()
        );
        dto.setNumberOfExistingFacilities(
                entity.getNumberOfExistingFacilities()
        );
        dto.setDebtServiceRatio(entity.getDebtServiceRatio());
        dto.setCoverageRatio(entity.getCoverageRatio());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}