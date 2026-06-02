package com.rutusoft.flowable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private Long id;
    private String productName;
    private String description;

    private String category;
    private String productType;
    private Double rateType;

    private String productCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}