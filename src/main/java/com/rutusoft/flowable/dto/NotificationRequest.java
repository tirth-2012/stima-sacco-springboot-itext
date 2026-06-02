package com.rutusoft.flowable.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NotificationRequest {

    @NotBlank(message = "processInstanceId is required")
    private String processInstanceId;

    @NotBlank(message = "referenceNo is required")
    private String referenceNo;

    @NotBlank(message = "notification is required")
    private String notification;

    @NotBlank(message = "sentTo is required")
    private String sentTo;

    @NotBlank(message = "sentFrom is required")
    private String sentFrom;
}
