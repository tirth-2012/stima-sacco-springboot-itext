package com.rutusoft.flowable.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class CollateralRequest {
    private String processInstanceId;
    private String securityType;
    private String description;
    private String ownership;
    private String disbursementType;
    private String cifNumber;
    private Map<String, Object> detail;
}