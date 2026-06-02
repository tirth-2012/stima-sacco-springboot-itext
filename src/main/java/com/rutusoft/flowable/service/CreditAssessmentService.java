package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.RiskAssessmentDto;

import java.util.Map;

public interface CreditAssessmentService {
    RiskAssessmentDto generateCreditAssessment(String customerId);
}
