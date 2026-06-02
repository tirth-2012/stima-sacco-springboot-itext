package com.rutusoft.flowable.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutusoft.flowable.dto.CreditAssessmentDto;
import com.rutusoft.flowable.dto.RiskAssessmentDto;
import com.rutusoft.flowable.service.CreditAssessmentService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CreditAssessmentServiceImpl implements CreditAssessmentService {

    @Override
    public RiskAssessmentDto generateCreditAssessment(String customerId) {
        List<RiskAssessmentDto> riskAssessmentDtos = getSampleData();
        RiskAssessmentDto result = riskAssessmentDtos.stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .findFirst()
                .orElse(null);
        log.info("Fetched credit assessment for customer : {} is : {}", customerId, result);
        return result;
    }

    public static List<RiskAssessmentDto> getSampleData() {

        List<RiskAssessmentDto> list = new ArrayList<>();

        // Decision logic
        java.util.function.Function<Double, String> decisionFn = score -> {
            if (score < 40) return "Decline";
            if (score <= 60) return "Approve with Conditions";
            return "Approve";
        };

        // Risk Level
        java.util.function.Function<Double, String> riskLevelFn = score -> {
            if (score < 40) return "High";
            if (score <= 60) return "Medium";
            return "Low";
        };

        // Color
        java.util.function.Function<Double, String> colorFn = score -> {
            if (score < 40) return "Red";
            if (score <= 60) return "Amber";
            return "Green";
        };

        // -------- CIF2001 (Rejected) --------
        Map<String, Object> sc1 = new HashMap<>();
        sc1.put("score_card_character", 25);
        sc1.put("score_card_capacity", 60);
        sc1.put("score_card_collateral", 21);
        sc1.put("score_card_capital", 45);
        sc1.put("score_card_conditions", 30);
        sc1.put("score_card_composite_score", 36.58);
        sc1.put("score_card_risk_band", "Rejected");

        list.add(create("CIF-3035", riskLevelFn.apply(36.58), colorFn.apply(36.58), 36,
                decisionFn.apply(36.58),
                "Low repayment capacity and weak financial profile.",
                Arrays.asList("High obligations", "Low stability"),
                Arrays.asList("Improve income stability"),
                sc1,
                "DSR above acceptable limit.",
                "Weak collateral.",
                "Compliant."));

        // -------- CIF2002 (Good) --------
        Map<String, Object> sc2 = new HashMap<>();
        sc2.put("score_card_character", 60);
        sc2.put("score_card_capacity", 70);
        sc2.put("score_card_collateral", 65);
        sc2.put("score_card_capital", 72);
        sc2.put("score_card_conditions", 45);
        sc2.put("score_card_composite_score", 63.77);
        sc2.put("score_card_risk_band", "Good");

        list.add(create("CIF-3036", riskLevelFn.apply(63.77), colorFn.apply(63.77), 63,
                decisionFn.apply(63.77),
                "Moderate repayment capacity with manageable DSR.",
                Arrays.asList("Single income source"),
                Arrays.asList("Provide salary slips", "Employment verification"),
                sc2,
                "DSR within acceptable range.",
                "Adequate collateral.",
                "Compliant."));

        // -------- CIF2003 (Good variation) --------
        Map<String, Object> sc3 = new HashMap<>();
        sc3.put("score_card_character", 62);
        sc3.put("score_card_capacity", 68);
        sc3.put("score_card_collateral", 60);
        sc3.put("score_card_capital", 70);
        sc3.put("score_card_conditions", 50);
        sc3.put("score_card_composite_score", 64.20);
        sc3.put("score_card_risk_band", "Good");

        list.add(create("CIF-3037", riskLevelFn.apply(64.20), colorFn.apply(64.20), 64,
                decisionFn.apply(64.20),
                "Stable financial position with acceptable risk.",
                Arrays.asList("Moderate DSR"),
                Arrays.asList("Monitor repayment"),
                sc3,
                "Within limit.",
                "Sufficient collateral.",
                "Compliant."));

        // -------- CIF2004 (Borderline Good) --------
        Map<String, Object> sc4 = new HashMap<>();
        sc4.put("score_card_character", 65);
        sc4.put("score_card_capacity", 60);
        sc4.put("score_card_collateral", 50);
        sc4.put("score_card_capital", 72);
        sc4.put("score_card_conditions", 75);
        sc4.put("score_card_composite_score", 61.77);
        sc4.put("score_card_risk_band", "Good");

        list.add(create("CIF-3038", riskLevelFn.apply(61.77), colorFn.apply(61.77), 61,
                decisionFn.apply(61.77),
                "Borderline DSR with moderate employment stability.",
                Arrays.asList("Borderline DSR"),
                Arrays.asList("Employment confirmation"),
                sc4,
                "Needs monitoring.",
                "Adequate coverage.",
                "Compliant."));

        // -------- CIF2005 (Rejected) --------
        Map<String, Object> sc5 = new HashMap<>();
        sc5.put("score_card_character", 20);
        sc5.put("score_card_capacity", 25);
        sc5.put("score_card_collateral", 30);
        sc5.put("score_card_capital", 20);
        sc5.put("score_card_conditions", 30);
        sc5.put("score_card_composite_score", 24.25);
        sc5.put("score_card_risk_band", "Rejected");

        list.add(create("CIF-3039", riskLevelFn.apply(24.25), colorFn.apply(24.25), 24,
                decisionFn.apply(24.25),
                "Very weak financial profile and high default risk.",
                Arrays.asList("Low income", "High liabilities"),
                Arrays.asList("Reduce obligations"),
                sc5,
                "DSR exceeds threshold.",
                "Insufficient collateral.",
                "Not viable."));

        // -------- CIF2006 (Good) --------
        Map<String, Object> sc6 = new HashMap<>();
        sc6.put("score_card_character", 75);
        sc6.put("score_card_capacity", 65);
        sc6.put("score_card_collateral", 50);
        sc6.put("score_card_capital", 55);
        sc6.put("score_card_conditions", 70);
        sc6.put("score_card_composite_score", 65.13);
        sc6.put("score_card_risk_band", "Good");

        list.add(create("CIF-3041", riskLevelFn.apply(65.13), colorFn.apply(65.13), 65,
                decisionFn.apply(65.13),
                "Moderate risk with stable income profile.",
                Arrays.asList("Income dependency"),
                Arrays.asList("Add co-applicant"),
                sc6,
                "Within limit.",
                "Satisfactory.",
                "Compliant."));

        // -------- CIF2007 (Excellent) --------
        Map<String, Object> sc7 = new HashMap<>();
        sc7.put("score_card_character", 95);
        sc7.put("score_card_capacity", 85);
        sc7.put("score_card_collateral", 80);
        sc7.put("score_card_capital", 65);
        sc7.put("score_card_conditions", 90);
        sc7.put("score_card_composite_score", 86.38);
        sc7.put("score_card_risk_band", "Excellent");

        list.add(create("CIF2007", riskLevelFn.apply(86.38), colorFn.apply(86.38), 86,
                decisionFn.apply(86.38),
                "Strong financial profile with excellent repayment capacity.",
                Arrays.asList("Minor income variability"),
                Arrays.asList("Periodic monitoring"),
                sc7,
                "Very healthy DSR.",
                "Strong collateral.",
                "Compliant."));

        // -------- CIF2008 (Rejected variation) --------
        Map<String, Object> sc8 = new HashMap<>();
        sc8.put("score_card_character", 22);
        sc8.put("score_card_capacity", 28);
        sc8.put("score_card_collateral", 35);
        sc8.put("score_card_capital", 25);
        sc8.put("score_card_conditions", 32);
        sc8.put("score_card_composite_score", 26.10);
        sc8.put("score_card_risk_band", "Rejected");

        list.add(create("CIF2008", riskLevelFn.apply(26.10), colorFn.apply(26.10), 26,
                decisionFn.apply(26.10),
                "Weak profile with insufficient repayment capacity.",
                Arrays.asList("Low income stability"),
                Arrays.asList("Improve financials"),
                sc8,
                "DSR too high.",
                "Weak security.",
                "Not viable."));

        // -------- CIF2009 (Excellent) --------
        Map<String, Object> sc9 = new HashMap<>();
        sc9.put("score_card_character", 85);
        sc9.put("score_card_capacity", 95);
        sc9.put("score_card_collateral", 85);
        sc9.put("score_card_capital", 75);
        sc9.put("score_card_conditions", 90);
        sc9.put("score_card_composite_score", 87.63);
        sc9.put("score_card_risk_band", "Excellent");

        list.add(create("CIF2009", riskLevelFn.apply(87.63), colorFn.apply(87.63), 87,
                decisionFn.apply(87.63),
                "Strong profile with minor monitoring required.",
                Arrays.asList("High exposure"),
                Arrays.asList("Restrict further borrowing"),
                sc9,
                "Near threshold.",
                "Adequate.",
                "Compliant."));

        // -------- CIF2010 (Excellent) --------
        Map<String, Object> sc10 = new HashMap<>();
        sc10.put("score_card_character", 75);
        sc10.put("score_card_capacity", 95);
        sc10.put("score_card_collateral", 90);
        sc10.put("score_card_capital", 75);
        sc10.put("score_card_conditions", 85);
        sc10.put("score_card_composite_score", 84.75);
        sc10.put("score_card_risk_band", "Excellent");

        list.add(create("CIF2010", riskLevelFn.apply(84.75), colorFn.apply(84.75), 84,
                decisionFn.apply(84.75),
                "Good repayment capacity and stable employment.",
                Arrays.asList("Seasonal income variation"),
                Arrays.asList("Periodic review"),
                sc10,
                "Comfortable DSR.",
                "Strong collateral.",
                "Compliant."));

        return list;
    }

    private static RiskAssessmentDto create(
            String customerId,
            String rating,
            String badge,
            int score,
            String recommendation,
            String summary,
            List<String> risks,
            List<String> conditions,
            Map<String, Object> scorecard,
            String dsrComment,
            String collateralComment,
            String shariahNote
    ) {

        RiskAssessmentDto r = new RiskAssessmentDto();
        r.setCustomerId(customerId);

        RiskAssessmentDto.Label riskLabel = new RiskAssessmentDto.Label();
        riskLabel.setValue(rating);
        riskLabel.setBadge(badge);

        RiskAssessmentDto.Score riskScore = new RiskAssessmentDto.Score();
        riskScore.setScore(score);
        riskScore.setMaxScore(100);

        r.setScorecard(scorecard);

        RiskAssessmentDto.Label recLabel = new RiskAssessmentDto.Label();
        recLabel.setValue(recommendation);
        recLabel.setBadge(badge);

        r.setRiskRating(riskLabel);
        r.setRiskScore(riskScore);
        r.setRecommendation(recLabel);

        r.setSummary(summary);
        r.setKeyRisks(risks);
        r.setConditions(conditions);

        r.setDsrComment(dsrComment);
        r.setCollateralComment(collateralComment);
        r.setShariahNote(shariahNote);

        return r;
    }

    public void generateCreditAssessment(DelegateExecution execution) {
        log.info("Generating credit score assessment");
        String cifNumber = execution.getVariable("cif_number") == null ? "CIF2001" : execution.getVariable("cif_number").toString();

        try {
            RiskAssessmentDto riskAssessmentDto = generateCreditAssessment(cifNumber);
            log.info("Credit score assessment : {}", riskAssessmentDto);

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> creditScoreAssessment = objectMapper.convertValue(
                   riskAssessmentDto,
                   new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                   }
           );
            execution.setVariable("creditScoreAssessment", creditScoreAssessment);
      } catch (Exception ex) {
            log.error("Error occurred while generating credit assessment : {}", ex.getMessage(), ex);
        }
    }
    
}
