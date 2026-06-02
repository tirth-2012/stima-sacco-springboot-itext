package com.rutusoft.flowable.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "guarantors")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Guarantor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processInstanceId;

    // ---------------------------------------------------------
    // GUARANTOR DETAILS
    // ---------------------------------------------------------
    private String fullName;

    // CIF Number of guarantor
    private String memberNumber;

    private String guaranteeId;

    private String mobileNumber;

    private BigDecimal guarantorAmount;

    /**
     * PENDING
     * APPROVED
     * REJECTED
     */
    private String status;

    // ---------------------------------------------------------
    // BORROWER DETAILS
    // ---------------------------------------------------------
    private String borrowerName;

    private String borrowerMemberNumber;

    private String borrowerMobileNumber;

    private String borrowerNationalId;

    // ---------------------------------------------------------
    // Customer Relation
    // ---------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Audit
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}