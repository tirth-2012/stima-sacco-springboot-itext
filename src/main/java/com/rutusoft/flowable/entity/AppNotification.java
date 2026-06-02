package com.rutusoft.flowable.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifications")
public class AppNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String processInstanceId;
    private String referenceNo;
    private String notification;
    private String sentTo;
    private String sentFrom;
    @Column(name = "is_read")
    private boolean read = false;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime dateTime;

    private String subject;

    private String documentName;

    private String documentType;

    private Long documentId;

    private Long fileId;

    private String notificationType;

    private String email;
}
