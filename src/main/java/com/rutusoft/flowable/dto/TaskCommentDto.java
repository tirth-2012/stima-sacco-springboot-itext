package com.rutusoft.flowable.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TaskCommentDto {
    private String commentId;
    private String taskId;
    private String processInstanceId;
    private String message;
    private String type;
    private String userId;
    private Date commentTime;
}
