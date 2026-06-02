package com.rutusoft.flowable.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GuarantorResponseDto {

    private Long id;

    // ---------------------------------------------------------
    // GUARANTOR DETAILS
    // ---------------------------------------------------------
    private String fullName;

    private String memberNumber;

    private String guaranteeId;

    private String mobileNumber;

    private BigDecimal guarantorAmount;

    private String status;

    // ---------------------------------------------------------
    // BORROWER DETAILS
    // ---------------------------------------------------------
    private String borrowerName;

    private String borrowerMemberNumber;

    private String borrowerMobileNumber;

    private String borrowerNationalId;

    // ---------------------------------------------------------
    // Customer Details
    // ---------------------------------------------------------
    private Long customerId;

    private String customerName;

    private String customerEmail;

    private String customerMobile;

    private String nationalId;

    private String customerType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}