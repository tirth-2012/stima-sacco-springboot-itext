package com.rutusoft.flowable.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GuarantorRequestDto {
    @NotBlank(message = "Process instance id is required")
    private String processIntanceId;

    @NotBlank(message = "Member number is required")
    private String memberNumber;

    @NotNull(message = "Guarantor amount is required")
    private BigDecimal guarantorAmount;

    private String guaranteeId;

    private String status;
}