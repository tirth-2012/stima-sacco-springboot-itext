package com.rutusoft.flowable.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanFinancialDetailsRequestDto {

    private Long loanApplicationId;

    private BigDecimal monthlyNetIncome;

    private BigDecimal monthlyBusinessRevenue;

    private BigDecimal annualTurnover;

    private Integer yearsOfBusiness;

    private BigDecimal existingMonthlyObligations;

    private Integer numberOfExistingFacilities;

    private BigDecimal debtServiceRatio;

    private BigDecimal coverageRatio;
}