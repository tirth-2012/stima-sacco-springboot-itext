package com.rutusoft.flowable.entity;


import com.rutusoft.flowable.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_action_log",
        indexes = {
                @Index(name = "idx_process_instance", columnList = "process_instance_id"),
                @Index(name = "idx_task_id", columnList = "task_id"),
                @Index(name = "idx_action_time", columnList = "action_time")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // Workflow Reference
    // =========================
    @NotNull
    @Column(name = "process_instance_id", nullable = false, length = 100)
    private String processInstanceId;

    @NotNull
    @Column(name = "task_id", nullable = false, length = 100)
    private String taskId;

    @Column(name = "task_name", length = 255)
    private String taskName;

    // =========================
    // Action Details
    // =========================
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private ActionType action;

    @NotNull
    @Column(name = "action_by", nullable = false, length = 100)
    private String actionBy;

    @Column(name = "action_by_name", length = 255)
    private String actionByName;

    @NotNull
    @Column(name = "action_time", nullable = false)
    private LocalDateTime actionTime;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "step_back_to_stage", length = 255)
    private String stepBackToStage;

    // =========================
    // Audit Fields
    // =========================
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // =========================
    // Lifecycle Hooks
    // =========================
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.actionTime = (this.actionTime == null) ? LocalDateTime.now() : this.actionTime;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
