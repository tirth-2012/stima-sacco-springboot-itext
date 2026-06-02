package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.EligibilityDTO;

public interface EligibilityService {
    EligibilityDTO fetchEligibility(String customerId);
}
