package com.rutusoft.flowable.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "application_draft")

@Data
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ApplicationDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "draft_id", unique = true)
    private String draftId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "current_step")
    private Integer currentStep;

    @Column(name = "status")
    private String status;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Object formData;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}