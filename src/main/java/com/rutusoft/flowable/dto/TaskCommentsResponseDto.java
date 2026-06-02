package com.rutusoft.flowable.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskCommentsResponseDto {
    private List<TaskCommentDto> taskComments;

}
