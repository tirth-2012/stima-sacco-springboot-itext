package com.rutusoft.flowable.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LoanFinancialDetailsResponseDto {

    private Long id;

    private Long loanApplicationId;

    private BigDecimal monthlyNetIncome;

    private BigDecimal monthlyBusinessRevenue;

    private BigDecimal annualTurnover;

    private Integer yearsOfBusiness;

    private BigDecimal existingMonthlyObligations;

    private Integer numberOfExistingFacilities;

    private BigDecimal debtServiceRatio;

    private BigDecimal coverageRatio;

    private LocalDateTime createdAt;
}