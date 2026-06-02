package com.rutusoft.flowable.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String processInstanceId;
    private String referenceNo;
    private String notification;
    private String sentTo;
    private String sentFrom;
    private boolean read;
    private LocalDateTime dateTime;
}
