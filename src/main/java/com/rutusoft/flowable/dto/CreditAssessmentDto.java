package com.rutusoft.flowable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditAssessmentDto {
    private String customerId;

    // 🔹 CRB Data
    private Integer crbBureauScore; // from CRB Africa

    // 🔹 Flexcube Data
    private String repaymentHistory;        // e.g. EXCELLENT / GOOD / POOR
    private String borrowingExperience;     // e.g. "5 Years"
    private String arrearsOrPenalties;      // e.g. NONE / LOW / HIGH
    private Integer bouncedCheques;         // count
    private String financingGraduation;     // e.g. "3 Facilities"

    // 🔹 Calculated Fields
    private Double dsr;                     // Debt Service Ratio (%)
    private Double existingObligations;
    private Double proposedInstallment;
    private Double netMonthlyIncome;

    private Integer borrowerAge;           // calculated from DOB
    private String ageBand;                // e.g. "25-40"

    private Double collateralValue;
    private Double financingAmount;
    private Double collateralToFinancingRatio; // %

}