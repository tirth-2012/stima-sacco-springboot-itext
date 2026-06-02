package com.rutusoft.flowable.dto;

import lombok.Data;

@Data
public class EligibilityDTO {
    private String customerId;

    private Integer memberCharacter;
    private String memberCharacterDescription;
    private Integer memberCharacterWeightage;

    private Integer depositeSavings;
    private String depositeSavingsDescription;
    private Integer depositeSavingsWeightage;

    private Integer repaymentCapacity;
    private String repaymentCapacityDescription;
    private Integer repaymentCapacityWeightage;

    private Integer guarantorStrength;
    private String guarantorStrengthDescription;
    private Integer guarantorStrengthWeightage;

    private Integer employmentPayroll;
    private String employmentPayrollDescription;
    private Integer employmentPayrollWeightage;

    private Integer existingSACCOExposure;
    private String existingSACCOExposureDescription;
    private Integer existingSACCOExposureWeightage;

    private Integer repaymentHistory;
    private String repaymentHistoryDescription;
    private Integer repaymentHistoryWeightage;

    private Integer collateralSecurity;
    private String collateralSecurityDescription;
    private Integer collateralSecurityWeightage;
}
