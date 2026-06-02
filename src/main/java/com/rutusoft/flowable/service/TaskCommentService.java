package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.TaskCommentCreateDto;
import com.rutusoft.flowable.dto.TaskCommentsResponseDto;

public interface TaskCommentService {
    String addTaskComment(TaskCommentCreateDto taskCommentCreateDto);
    TaskCommentsResponseDto getTaskComments(String taskId);
    String deleteTaskComment(String commentId);
}
