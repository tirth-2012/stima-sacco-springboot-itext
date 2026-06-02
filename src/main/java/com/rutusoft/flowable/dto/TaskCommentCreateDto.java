package com.rutusoft.flowable.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TaskCommentCreateDto {
    private String taskId;
    private String processInstanceId;
    private String message;
    private String type;
    private String userId;
}
