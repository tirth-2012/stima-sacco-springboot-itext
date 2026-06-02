package com.rutusoft.flowable.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "collaterals")
@Data
public class Collateral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processInstanceId;

    // Common Fields
    private String securityType; // MOTOR_VEHICLE, PROPERTY, CASH, EQUIPMENT
    private String description;
    private String ownership;
    private String disbursementType;
    private String cifNumber;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Object detail;

    // Mapping
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}