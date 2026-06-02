package com.rutusoft.flowable.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerResponseDto {

    private Long id;

    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;

    private String nationalId;
    private String kraPin;

    private String email;
    private String mobileNumber;

    private String physicalAddress;
    private String postalAddress;

    private String nationality;
    private String maritalStatus;

    private String cifNumber;
    private String customerType;
    private String accountSince;

    private Boolean existingCustomer;
    private Boolean kycVerified;
    private String status;

    private String intakeChannel;
    private String relationshipManager;

    private Integer existingFacilities;
    private String totalExposure;
    private String repaymentRecord;
    private String lastFacility;

    private Double loanAmountLimit;
    private Double availableLoanLimit;

    private String bankName;
    private String branchName;
    private String accountNumber;
    private String accountType;
    private String swiftCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CustomerObligationResponseDto> obligations;
}