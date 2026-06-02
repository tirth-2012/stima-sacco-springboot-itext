package com.rutusoft.flowable.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class RiskAssessmentDto {

    private String customerId;

    private Label riskRating;
    private Score riskScore;
    private Label recommendation;

    private String summary;

    private List<String> keyRisks;
    private List<String> conditions;
    private Map<String, Object> scorecard;

    private String dsrComment;
    private String collateralComment;
    private String shariahNote;

    @Data
    public static class Label {
        private String value;
        private String badge;
    }

    @Data
    public static class Score {
        private int score;
        private int maxScore;
    }
}