package com.rutusoft.flowable.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationDto {
    private String processInstanceId;
    private String referenceId;
    private String fullname;
    private Integer loanAmount;
    private String productType;
}