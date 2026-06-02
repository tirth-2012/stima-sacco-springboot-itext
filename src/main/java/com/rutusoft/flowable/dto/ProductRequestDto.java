package com.rutusoft.flowable.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class ProductRequestDto {
    private String productName;
    private String description;

    private String category;     // Shariah Compliant / Conventional
    private String productType;  // Islamic / Conventional Financing

    @NotNull(message = "Rate type is required")
    @Positive(message = "Rate must be positive")
    private Double rateType;

    private String productCode;
}
