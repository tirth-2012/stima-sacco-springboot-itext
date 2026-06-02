package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.TaskCommentCreateDto;
import com.rutusoft.flowable.dto.TaskCommentsResponseDto;
import com.rutusoft.flowable.service.TaskCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Task comment APIs",
        description = "APIs for managing Flowable human user task comments (add, retrieve and delete comments)"
)
@RestController
@RequestMapping("/tasks")
public class TaskCommentController {
    @Autowired
    private TaskCommentService taskCommentService;

    // ----------------------------------------------------------------------
    // Comments
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Add task comment",
            description = "Adds a comment to a Flowable task"
    )
    @ApiResponse(responseCode = "200", description = "Comment added successfully")
    @PostMapping("/comment")
    public ResponseEntity<String> addTaskComment(
            @RequestBody TaskCommentCreateDto taskCommentCreateDto) {

        return new ResponseEntity<>(
                taskCommentService.addTaskComment(taskCommentCreateDto),
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Get task comments",
            description = "Retrieves all comments for a given task"
    )
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<TaskCommentsResponseDto> getTaskComments(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("taskId") String taskId) {

        return new ResponseEntity<>(
                taskCommentService.getTaskComments(taskId),
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Delete task comments",
            description = "Deletes all comments associated with a task"
    )
    @ApiResponse(responseCode = "200", description = "Comments deleted successfully")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteTaskComment(
            @Parameter(description = "Comment ID", required = true)
            @PathVariable("commentId") String commentId) {

        return new ResponseEntity<>(
                taskCommentService.deleteTaskComment(commentId),
                HttpStatus.OK
        );
    }
}
