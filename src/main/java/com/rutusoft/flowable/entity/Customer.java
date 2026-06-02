package com.rutusoft.flowable.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customers"
//        ,
//        uniqueConstraints = {
//                @UniqueConstraint(columnNames = "email"),
//                @UniqueConstraint(columnNames = "mobileNumber"),
//                @UniqueConstraint(columnNames = "nationalId")
//        }

        )
@EntityListeners(AuditingEntityListener.class)
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Basic Info
    private String fullName;

    private String gender;

    private LocalDate dateOfBirth;

    // 🔹 Identity
    @Column(nullable = false, unique = true)
    private String nationalId;

    private String kraPin;

    // 🔹 Contact
   //@Column(unique = true)
    private String email;

    @Column(unique = true)
    private String mobileNumber;

    // 🔹 Address
    private String physicalAddress;
    private String postalAddress;

    // 🔹 Other Info
    private String nationality;
    private String maritalStatus;

    // 🔹 Banking
    @Column(unique = true)
    private String cifNumber;

    private String customerType;
    private String accountSince;

    // 🔹 Flags
    private Boolean existingCustomer;
    private Boolean kycVerified;
    private String status;

    // 🔹 Application-like
    private String intakeChannel;
    private String relationshipManager;

    // 🔹 Financial Snapshot
    private Integer existingFacilities;
    private String totalExposure;
    private String repaymentRecord;
    private String lastFacility;

    // 🔹 Loan Limits
    @Column(name = "loan_amount_limit")
    private Double loanAmountLimit = 0.0;

    @Column(name = "available_loan_limit")
    private Double availableLoanLimit = 0.0;

    private String bankName;
    private String branchName;
    private String accountNumber;
    private String accountType;
    private String swiftCode;

    // 🔹 Audit
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<CustomerObligation> obligations;

    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Collateral> collaterals;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Guarantor> guarantors;
}