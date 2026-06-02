package com.rutusoft.flowable.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "customer_obligations")
@Data
public class CustomerObligation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔹 Relation with Customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // 🔹 Fields
    private String cifNumber;

    @Column(name = "lender")
    private String lender;

    private String facilityType;

    private Double outstanding;
    private Double monthlyCommitment;

    private String source;
    private String status;
}