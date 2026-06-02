package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.EligibilityDTO;
import com.rutusoft.flowable.dto.RiskAssessmentDto;
import com.rutusoft.flowable.service.EligibilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EligibilityServiceImpl implements EligibilityService {

    @Override
    public EligibilityDTO fetchEligibility(String customerId) {
        List<EligibilityDTO> eligibilityDTOS = getSampleData();
        EligibilityDTO result = eligibilityDTOS.stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .findFirst()
                .orElse(null);
        //log.info("Fetched eligibility data for customer : {} is : {}", customerId, result);
        return result;
    }

    private List<EligibilityDTO> getSampleData() {
        List<EligibilityDTO> eligibilityDTOS = new ArrayList<>();

        //Customer eligibility: STM-00038111
        EligibilityDTO eligibilityDTO1 = new EligibilityDTO();
        eligibilityDTO1.setCustomerId("STM-00038111");
        eligibilityDTO1.setMemberCharacter(95);
        eligibilityDTO1.setMemberCharacterDescription("Member Character");
        eligibilityDTO1.setMemberCharacterWeightage(10);

        eligibilityDTO1.setDepositeSavings(94);
        eligibilityDTO1.setDepositeSavingsDescription("Deposits & Savings");
        eligibilityDTO1.setDepositeSavingsWeightage(20);

        eligibilityDTO1.setRepaymentCapacity(96);
        eligibilityDTO1.setRepaymentCapacityDescription("Repayment Capacity");
        eligibilityDTO1.setRepaymentCapacityWeightage(20);

        eligibilityDTO1.setGuarantorStrength(35);
        eligibilityDTO1.setGuarantorStrengthDescription("Guarantor Strength");
        eligibilityDTO1.setGuarantorStrengthWeightage(10);

        eligibilityDTO1.setEmploymentPayroll(90);
        eligibilityDTO1.setEmploymentPayrollDescription("Employment / Payroll");
        eligibilityDTO1.setEmploymentPayrollWeightage(15);

        eligibilityDTO1.setExistingSACCOExposure(88);
        eligibilityDTO1.setExistingSACCOExposureDescription("Existing SACCO Exposure");
        eligibilityDTO1.setExistingSACCOExposureWeightage(10);

        eligibilityDTO1.setRepaymentHistory(96);
        eligibilityDTO1.setRepaymentHistoryDescription("Repayment History");
        eligibilityDTO1.setRepaymentHistoryWeightage(10);

        eligibilityDTO1.setCollateralSecurity(85);
        eligibilityDTO1.setCollateralSecurityDescription("Collateral / Security ");
        eligibilityDTO1.setCollateralSecurityWeightage(5);

        //Customer eligibility: STM-00038112
        EligibilityDTO eligibilityDTO2 = new EligibilityDTO();
        eligibilityDTO2.setCustomerId("STM-00038112");
        eligibilityDTO2.setMemberCharacter(96);
        eligibilityDTO2.setMemberCharacterDescription("Member Character");
        eligibilityDTO2.setMemberCharacterWeightage(10);

        eligibilityDTO2.setDepositeSavings(97);
        eligibilityDTO2.setDepositeSavingsDescription("Deposits & Savings");
        eligibilityDTO2.setDepositeSavingsWeightage(20);

        eligibilityDTO2.setRepaymentCapacity(95);
        eligibilityDTO2.setRepaymentCapacityDescription("Repayment Capacity");
        eligibilityDTO2.setRepaymentCapacityWeightage(20);

        eligibilityDTO2.setGuarantorStrength(80);
        eligibilityDTO2.setGuarantorStrengthDescription("Guarantor Strength");
        eligibilityDTO2.setGuarantorStrengthWeightage(10);

        eligibilityDTO2.setEmploymentPayroll(97);
        eligibilityDTO2.setEmploymentPayrollDescription("Employment / Payroll");
        eligibilityDTO2.setEmploymentPayrollWeightage(15);

        eligibilityDTO2.setExistingSACCOExposure(83);
        eligibilityDTO2.setExistingSACCOExposureDescription("Existing SACCO Exposure");
        eligibilityDTO2.setExistingSACCOExposureWeightage(10);

        eligibilityDTO2.setRepaymentHistory(91);
        eligibilityDTO2.setRepaymentHistoryDescription("Repayment History");
        eligibilityDTO2.setRepaymentHistoryWeightage(10);

        eligibilityDTO2.setCollateralSecurity(95);
        eligibilityDTO2.setCollateralSecurityDescription("Collateral / Security ");
        eligibilityDTO2.setCollateralSecurityWeightage(5);

        //Customer eligibility: STM-00038113
        EligibilityDTO eligibilityDTO3 = new EligibilityDTO();
        eligibilityDTO3.setCustomerId("STM-00038113");
        eligibilityDTO3.setMemberCharacter(93);
        eligibilityDTO3.setMemberCharacterDescription("Member Character");
        eligibilityDTO3.setMemberCharacterWeightage(10);

        eligibilityDTO3.setDepositeSavings(95);
        eligibilityDTO3.setDepositeSavingsDescription("Deposits & Savings");
        eligibilityDTO3.setDepositeSavingsWeightage(20);

        eligibilityDTO3.setRepaymentCapacity(92);
        eligibilityDTO3.setRepaymentCapacityDescription("Repayment Capacity");
        eligibilityDTO3.setRepaymentCapacityWeightage(20);

        eligibilityDTO3.setGuarantorStrength(89);
        eligibilityDTO3.setGuarantorStrengthDescription("Guarantor Strength");
        eligibilityDTO3.setGuarantorStrengthWeightage(10);

        eligibilityDTO3.setEmploymentPayroll(91);
        eligibilityDTO3.setEmploymentPayrollDescription("Employment / Payroll");
        eligibilityDTO3.setEmploymentPayrollWeightage(15);

        eligibilityDTO3.setExistingSACCOExposure(93);
        eligibilityDTO3.setExistingSACCOExposureDescription("Existing SACCO Exposure");
        eligibilityDTO3.setExistingSACCOExposureWeightage(10);

        eligibilityDTO3.setRepaymentHistory(90);
        eligibilityDTO3.setRepaymentHistoryDescription("Repayment History");
        eligibilityDTO3.setRepaymentHistoryWeightage(10);

        eligibilityDTO3.setCollateralSecurity(95);
        eligibilityDTO3.setCollateralSecurityDescription("Collateral / Security ");
        eligibilityDTO3.setCollateralSecurityWeightage(5);

        //Customer eligibility: STM-00038114
        EligibilityDTO eligibilityDTO4 = new EligibilityDTO();
        eligibilityDTO4.setCustomerId("STM-00038114");
        eligibilityDTO4.setMemberCharacter(91);
        eligibilityDTO4.setMemberCharacterDescription("Member Character");
        eligibilityDTO4.setMemberCharacterWeightage(10);

        eligibilityDTO4.setDepositeSavings(97);
        eligibilityDTO4.setDepositeSavingsDescription("Deposits & Savings");
        eligibilityDTO4.setDepositeSavingsWeightage(20);

        eligibilityDTO4.setRepaymentCapacity(93);
        eligibilityDTO4.setRepaymentCapacityDescription("Repayment Capacity");
        eligibilityDTO4.setRepaymentCapacityWeightage(20);

        eligibilityDTO4.setGuarantorStrength(90);
        eligibilityDTO4.setGuarantorStrengthDescription("Guarantor Strength");
        eligibilityDTO4.setGuarantorStrengthWeightage(10);

        eligibilityDTO4.setEmploymentPayroll(98);
        eligibilityDTO4.setEmploymentPayrollDescription("Employment / Payroll");
        eligibilityDTO4.setEmploymentPayrollWeightage(15);

        eligibilityDTO4.setExistingSACCOExposure(90);
        eligibilityDTO4.setExistingSACCOExposureDescription("Existing SACCO Exposure");
        eligibilityDTO4.setExistingSACCOExposureWeightage(10);

        eligibilityDTO4.setRepaymentHistory(94);
        eligibilityDTO4.setRepaymentHistoryDescription("Repayment History");
        eligibilityDTO4.setRepaymentHistoryWeightage(10);

        eligibilityDTO4.setCollateralSecurity(93);
        eligibilityDTO4.setCollateralSecurityDescription("Collateral / Security");
        eligibilityDTO4.setCollateralSecurityWeightage(5);

        //Customer eligibility: STM-00038115
        EligibilityDTO eligibilityDTO5 = new EligibilityDTO();
        eligibilityDTO5.setCustomerId("STM-00038115");
        eligibilityDTO5.setMemberCharacter(95);
        eligibilityDTO5.setMemberCharacterDescription("Member Character");
        eligibilityDTO5.setMemberCharacterWeightage(10);

        eligibilityDTO5.setDepositeSavings(93);
        eligibilityDTO5.setDepositeSavingsDescription("Deposits & Savings");
        eligibilityDTO5.setDepositeSavingsWeightage(20);

        eligibilityDTO5.setRepaymentCapacity(91);
        eligibilityDTO5.setRepaymentCapacityDescription("Repayment Capacity");
        eligibilityDTO5.setRepaymentCapacityWeightage(20);

        eligibilityDTO5.setGuarantorStrength(95);
        eligibilityDTO5.setGuarantorStrengthDescription("Guarantor Strength");
        eligibilityDTO5.setGuarantorStrengthWeightage(10);

        eligibilityDTO5.setEmploymentPayroll(89);
        eligibilityDTO5.setEmploymentPayrollDescription("Employment / Payroll");
        eligibilityDTO5.setEmploymentPayrollWeightage(15);

        eligibilityDTO5.setExistingSACCOExposure(91);
        eligibilityDTO5.setExistingSACCOExposureDescription("Existing SACCO Exposure");
        eligibilityDTO5.setExistingSACCOExposureWeightage(10);

        eligibilityDTO5.setRepaymentHistory(97);
        eligibilityDTO5.setRepaymentHistoryDescription("Repayment History");
        eligibilityDTO5.setRepaymentHistoryWeightage(10);

        eligibilityDTO5.setCollateralSecurity(90);
        eligibilityDTO5.setCollateralSecurityDescription("Collateral / Security");
        eligibilityDTO5.setCollateralSecurityWeightage(5);

        //Customer eligibility: STM-00038116
        EligibilityDTO eligibilityDTO6 = new EligibilityDTO();
        eligibilityDTO6.setCustomerId("STM-00038111");
        eligibilityDTO6.setMemberCharacter(94);
        eligibilityDTO6.setMemberCharacterDescription("Member Character");
        eligibilityDTO6.setMemberCharacterWeightage(10);

        eligibilityDTO6.setDepositeSavings(91);
        eligibilityDTO6.setDepositeSavingsDescription("Deposits & Savings");
        eligibilityDTO6.setDepositeSavingsWeightage(20);

        eligibilityDTO6.setRepaymentCapacity(93);
        eligibilityDTO6.setRepaymentCapacityDescription("Repayment Capacity");
        eligibilityDTO6.setRepaymentCapacityWeightage(20);

        eligibilityDTO6.setGuarantorStrength(95);
        eligibilityDTO6.setGuarantorStrengthDescription("Guarantor Strength");
        eligibilityDTO6.setGuarantorStrengthWeightage(10);

        eligibilityDTO6.setEmploymentPayroll(96);
        eligibilityDTO6.setEmploymentPayrollDescription("Employment / Payroll");
        eligibilityDTO6.setEmploymentPayrollWeightage(15);

        eligibilityDTO6.setExistingSACCOExposure(95);
        eligibilityDTO6.setExistingSACCOExposureDescription("Existing SACCO Exposure");
        eligibilityDTO6.setExistingSACCOExposureWeightage(10);

        eligibilityDTO6.setRepaymentHistory(92);
        eligibilityDTO6.setRepaymentHistoryDescription("Repayment History");
        eligibilityDTO6.setRepaymentHistoryWeightage(10);

        eligibilityDTO6.setCollateralSecurity(98);
        eligibilityDTO6.setCollateralSecurityDescription("Collateral / Security");
        eligibilityDTO6.setCollateralSecurityWeightage(5);

        eligibilityDTOS.add(eligibilityDTO1);
        eligibilityDTOS.add(eligibilityDTO2);
        eligibilityDTOS.add(eligibilityDTO3);
        eligibilityDTOS.add(eligibilityDTO4);
        eligibilityDTOS.add(eligibilityDTO5);
        eligibilityDTOS.add(eligibilityDTO6);

        return eligibilityDTOS;
    }
}
