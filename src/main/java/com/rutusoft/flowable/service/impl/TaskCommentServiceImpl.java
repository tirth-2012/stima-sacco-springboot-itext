package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.TaskCommentCreateDto;
import com.rutusoft.flowable.dto.TaskCommentDto;
import com.rutusoft.flowable.dto.TaskCommentsResponseDto;
import com.rutusoft.flowable.service.TaskCommentService;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskCommentServiceImpl implements TaskCommentService {

    @Autowired
    private TaskService taskService;

    @Override
    public String addTaskComment(TaskCommentCreateDto taskCommentCreateDto) {
        Comment taskComment = taskService.addComment(taskCommentCreateDto.getTaskId(), taskCommentCreateDto.getProcessInstanceId(), taskCommentCreateDto.getType(), taskCommentCreateDto.getMessage());
        return "Task comment added successfully";
    }

    @Override
    public TaskCommentsResponseDto getTaskComments(String taskId) {
        List<Comment> taskComment = taskService.getTaskComments(taskId);
        TaskCommentsResponseDto taskCommentsResponseDto = new TaskCommentsResponseDto();
        List<TaskCommentDto> taskCommentDtos = new ArrayList<>();
        for(Comment comment : taskService.getTaskComments(taskId)) {
            TaskCommentDto taskCommentDto = new TaskCommentDto();

            taskCommentDto.setTaskId(comment.getTaskId());
            taskCommentDto.setProcessInstanceId(comment.getProcessInstanceId());
            taskCommentDto.setCommentId(comment.getId());
            taskCommentDto.setMessage(comment.getFullMessage());
            taskCommentDto.setCommentTime(comment.getTime());
            taskCommentDto.setUserId(comment.getUserId());
            taskCommentDto.setType(comment.getType());

            taskCommentDtos.add(taskCommentDto);
        }

        taskCommentsResponseDto.setTaskComments(taskCommentDtos);
        return taskCommentsResponseDto;
    }

    @Override
    public String deleteTaskComment(String commentId) {
        taskService.deleteComment(commentId);
        return "Comment deleted successfully";
    }
}
