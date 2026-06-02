package com.rutusoft.flowable.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "loan_applications")
@EntityListeners(AuditingEntityListener.class)
@Data
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(columnDefinition = "TEXT")
    private String loanPurposeDescription;

    @Column(columnDefinition = "TEXT")
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

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "loanApplication",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LoanFinancialDetails> financialDetails;
}