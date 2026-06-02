package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.TaskInstanceDto;
import com.rutusoft.flowable.dto.TaskInstancesResponseDto;
import com.rutusoft.flowable.service.HistoryTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historic-tasks")
public class HistoryTaskController {
    @Autowired
    private HistoryTaskService historyTaskService;

    @GetMapping("/completed/assignee/{assignee}")
    public ResponseEntity<TaskInstancesResponseDto> getAllActiveTasks(
            @Parameter(description = "Task assignee user", example = "manager")
            @PathVariable("assignee") String assignee,

            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam("from") int from,

            @Parameter(description = "Ending index for pagination", example = "10")
            @RequestParam("to") int to) {

        return new ResponseEntity<>(
                historyTaskService.getCompletedTaskByUserId(assignee, from, to),
                HttpStatus.OK
        );
    }

    @PostMapping("/all")
    public ResponseEntity<TaskInstancesResponseDto> getAllTasks(
            @Parameter(description = "Task assignee user", example = "manager")
            @RequestParam("assignee") String assignee,

            @Parameter(
                    description = "List of candidate groups",
                    required = true,
                    schema = @Schema(example = "[\"managers\", \"loan_officers\"]")
            )
            @RequestBody List<String> candidateGroups,

            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam("from") int from,

            @Parameter(description = "Ending index for pagination", example = "10")
            @RequestParam("to") int to) {

        return new ResponseEntity<>(
                historyTaskService.getAllTasks(assignee, candidateGroups, from, to),
                HttpStatus.OK
        );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskInstanceDto> getTaskById(
            @Parameter(description = "Task Id", example = "70953d43-29c9-11f1-81c1-c247406d9200")
            @PathVariable("taskId") String taskId) {

        return new ResponseEntity<>(
                historyTaskService.getTaskById(taskId),
                HttpStatus.OK
        );
    }



    @Operation(
            summary = "Get active tasks by process instance",
            description = "Retrieves all active tasks for a given process instance"
    )
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    @GetMapping("/process-instances/{processInstanceId}")
    public ResponseEntity<TaskInstancesResponseDto> getAllActiveTasksByProcessInstanceId(

            @Parameter(description = "Process instance ID", required = true)
            @PathVariable("processInstanceId") String processInstanceId,

            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam("from") int from,

            @Parameter(description = "Ending index for pagination", example = "10")
            @RequestParam("to") int to) {

        return new ResponseEntity<>(
                historyTaskService.getTaskInstancesByProcessInstanceId(processInstanceId, from, to),
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Get completed tasks by process instance",
            description = "Retrieves all completed tasks for a given process instance"
    )
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    @GetMapping("/completed/process-instances/{processInstanceId}")
    public ResponseEntity<TaskInstancesResponseDto> getAllCompletedTasksByProcessInstanceId(

            @Parameter(description = "Process instance ID", required = true)
            @PathVariable("processInstanceId") String processInstanceId,

            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam("from") int from,

            @Parameter(description = "Ending index for pagination", example = "10")
            @RequestParam("to") int to) {

        return new ResponseEntity<>(
                historyTaskService.getAllCompletedTasksByProcessInstanceId(processInstanceId, from, to),
                HttpStatus.OK
        );
    }

}
