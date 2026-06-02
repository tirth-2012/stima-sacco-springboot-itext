package com.rutusoft.flowable.dto;

import lombok.Data;

@Data
public class CustomerObligationResponseDto {

    private Long id;

    private String cifNumber;
    private String lender;
    private String facilityType;

    private Double outstanding;
    private Double monthlyCommitment;

    private String source;
    private String status;
}