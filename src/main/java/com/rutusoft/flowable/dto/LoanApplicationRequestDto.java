package com.rutusoft.flowable.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LoanApplicationRequestDto {

    private String referenceId;
    private String businessKey;
    private String processInstanceId;
    private String processDefinitionId;
    private Long customerId;
    private Long productId;
    private String requester;
    private String requesterFullName;
    private String rmUser;
    private Boolean applicationByCustomer;
    private String productType;
    private String productName;
    private String loanPurposeDescription;
    private String assetDescription;
    private BigDecimal costPrice;
    private BigDecimal profitRate;
    private BigDecimal profitAmount;
    private BigDecimal totalLoanAmount;
    private Integer financingTenor;
    private String paymentStructure;
    private BigDecimal monthlyInstallment;
    private BigDecimal proposedInstalment;
    private BigDecimal afterThisFacility;
    private String disbursementType;
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String accountType;
    private String swiftCode;
    private String customerCategory;
    private String businessSector;
    private String currentStage;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}