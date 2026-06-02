package com.rutusoft.flowable.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
public class CustomerRequestDto {

    // 🔹 Basic Info
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    // 🔹 Identity
    @NotBlank(message = "National ID is required")
    private String nationalId;

    private String kraPin;

    // 🔹 Contact
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;

    // 🔹 Address
    private String physicalAddress;
    private String postalAddress;

    // 🔹 Other Info
    private String nationality;
    private String maritalStatus;

    // 🔹 Banking
    @NotBlank(message = "CIF number is required")
    private String cifNumber;

    private String customerType;
    private String accountSince;

    // 🔹 Flags
    private Boolean existingCustomer;
    private Boolean kycVerified;

    private String status;

    // 🔹 Application-like
    private String intakeChannel;

    // 🔹 Financial Snapshot
    private Integer existingFacilities;
    private String totalExposure;
    private String repaymentRecord;
    private String lastFacility;

    //loam amount limit
    private Double loanAmountLimit;
    private Double availableLoanLimit;

    // RM
    private String relationshipManager;

    //BANK ACC DETAIL
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String accountType;
    private String swiftCode;

    //Obligation list
    private List<CustomerObligationRequestDto> obligations;
}