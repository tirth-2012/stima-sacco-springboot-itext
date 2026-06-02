package com.rutusoft.flowable.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_financial_details")
@EntityListeners(AuditingEntityListener.class)
@Data
public class LoanFinancialDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id")
    private LoanApplication loanApplication;

    @Column(precision = 18, scale = 2)
    private BigDecimal monthlyNetIncome;

    @Column(precision = 18, scale = 2)
    private BigDecimal monthlyBusinessRevenue;

    @Column(precision = 18, scale = 2)
    private BigDecimal annualTurnover;

    private Integer yearsOfBusiness;

    @Column(precision = 18, scale = 2)
    private BigDecimal existingMonthlyObligations;

    private Integer numberOfExistingFacilities;

    @Column(precision = 18, scale = 2)
    private BigDecimal debtServiceRatio;

    @Column(precision = 18, scale = 2)
    private BigDecimal coverageRatio;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}