package com.rutusoft.flowable.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CustomerObligationRequestDto {

    @NotBlank
    private String cifNumber;

    @NotBlank
    private String lender;

    @NotBlank
    private String facilityType;

    @NotNull
    private Double outstanding;

    @NotNull
    private Double monthlyCommitment;

    private String source;
    private String status;
}