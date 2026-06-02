package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.*;
import com.rutusoft.flowable.service.TaskInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(
        name = "Task APIs",
        description = "APIs for managing Flowable human tasks (query, claim, save, complete, comments, candidates)"
)
@RestController
@RequestMapping("/tasks")
public class TaskInstanceController {

    @Autowired
    private TaskInstanceService taskInstanceService;

    // ----------------------------------------------------------------------
    // Active Tasks
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Get all active tasks",
            description = "Retrieves all active Flowable tasks with pagination"
    )
    @ApiResponse(responseCode = "200", description = "Active tasks retrieved successfully")
    @GetMapping("/active")
    public ResponseEntity<TaskInstancesResponseDto> getAllActiveTasks(
            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam("from") int from,

            @Parameter(description = "Ending index for pagination", example = "10")
            @RequestParam("to") int to) {

        return new ResponseEntity<>(
                taskInstanceService.getAllActiveTasks(from, to),
                HttpStatus.OK
        );
    }

    // ----------------------------------------------------------------------
    // Active Tasks by Assignee
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Get active tasks by assignee",
            description = "Retrieves active Flowable tasks assigned to a specific user"
    )
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    @GetMapping("/active/assignee/{assignee}")
    public ResponseEntity<TaskInstancesResponseDto> getActiveTasksByAssignee(
            @Parameter(description = "Task assignee username", example = "john.doe")
            @PathVariable("assignee") String assignee,

            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam("from") int from,

            @Parameter(description = "Ending index for pagination", example = "10")
            @RequestParam("to") int to) {

        return new ResponseEntity<>(
                taskInstanceService.getActiveTasksByAssignee(assignee, from, to),
                HttpStatus.OK
        );
    }

    // ----------------------------------------------------------------------
    // Active Tasks by Candidate Groups
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Get active tasks by candidate groups",
            description = "Retrieves active tasks available for given candidate groups"
    )
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    @PostMapping("/active/candidate-group")
    public ResponseEntity<TaskInstancesResponseDto> getActiveTasksByCandidateGroups(

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
                taskInstanceService.getActiveTasksByCandidateGroups(candidateGroups, from, to),
                HttpStatus.OK
        );
    }

    // ----------------------------------------------------------------------
    // Active Tasks by Process Instance
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Get active tasks by process instance",
            description = "Retrieves all active tasks for a given process instance"
    )
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    @GetMapping("/active/process-instances/{processInstanceId}")
    public ResponseEntity<TaskInstancesResponseDto> getAllActiveTasksByProcessInstanceId(

            @Parameter(description = "Process instance ID", required = true)
            @PathVariable("processInstanceId") String processInstanceId,

            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam("from") int from,

            @Parameter(description = "Ending index for pagination", example = "10")
            @RequestParam("to") int to) {

        return new ResponseEntity<>(
                taskInstanceService.getAllActiveTasksByProcessInstanceId(processInstanceId, from, to),
                HttpStatus.OK
        );
    }

    // ----------------------------------------------------------------------
    // Active Tasks by Task ID
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Get active task by task id",
            description = "Retrieves active task for a given task id"
    )
    @ApiResponse(responseCode = "200", description = "Task retrieved successfully")
    @GetMapping("/active/{taskId}")
    public ResponseEntity<TaskInstanceDto> getActiveTasksByTaskId(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("taskId") String taskId
           ) {

        return new ResponseEntity<>(
                taskInstanceService.getTaskById(taskId),
                HttpStatus.OK
        );
    }

    // ----------------------------------------------------------------------
    // Claim / Unclaim
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Claim a task",
            description = "Claims a Flowable task for a specific user"
    )
    @ApiResponse(responseCode = "200", description = "Task claimed successfully")
    @PostMapping("/{taskId}/assignee/{assignee}/claim")
    public ResponseEntity<String> claimTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("taskId") String taskId,

            @Parameter(description = "Assignee username", required = true)
            @PathVariable("assignee") String assignee) {

        return new ResponseEntity<>(
                taskInstanceService.claimTask(taskId, assignee),
                HttpStatus.OK
        );
    }

    // ----------------------------------------------------------------------
    // Change task assignee
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Change task assignee",
            description = "Change task assignee"
    )
    @ApiResponse(responseCode = "200", description = "Task assignee changed successfully")
    @PostMapping("/{taskId}/assignee/{assignee}")
    public ResponseEntity<String> changeAssignee(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("taskId") String taskId,

            @Parameter(description = "Assignee username", required = true)
            @PathVariable("assignee") String assignee) {

        return new ResponseEntity<>(
                taskInstanceService.changeAssignee(taskId, assignee),
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Unclaim a task",
            description = "Removes assignee from a claimed task"
    )
    @ApiResponse(responseCode = "200", description = "Task unclaimed successfully")
    @PostMapping("/{taskId}/unclaim")
    public ResponseEntity<String> unclaimTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("taskId") String taskId) {

        return new ResponseEntity<>(
                taskInstanceService.unClaimTask(taskId),
                HttpStatus.OK
        );
    }

    // ----------------------------------------------------------------------
    // Save Task (taskService.saveTask)
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Save task details",
            description = """
            Saves updates to a Flowable task using taskService.saveTask().
            This does NOT complete the task.
            Typical use cases:
            - Update task name, description
            - Change due date or priority
            """
    )
    @ApiResponse(responseCode = "200", description = "Task saved successfully")
    @PostMapping("/save")
    public ResponseEntity<String> saveTask(
            @RequestBody TaskInstanceUpdateDto instanceUpdateDto) {

        return new ResponseEntity<>(
                taskInstanceService.saveTask(instanceUpdateDto),
                HttpStatus.OK
        );
    }

    // ----------------------------------------------------------------------
    // Complete Task
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Complete a task",
            description = "Completes a Flowable task and optionally sets process variables"
    )
    @ApiResponse(responseCode = "200", description = "Task completed successfully")
    @PostMapping("/{taskId}/complete")
    public ResponseEntity<String> completeTask(
            @Parameter(description = "Task ID", required = true)
            @PathVariable("taskId") String taskId,

            @Parameter(
                    description = "Process variables to be set on completion",
                    schema = @Schema(example = "{\"approved\": true}")
            )
            @RequestBody Map<String, Object> variables) {

        return new ResponseEntity<>(
                taskInstanceService.completeTask(taskId, variables),
                HttpStatus.OK
        );
    }



    // ----------------------------------------------------------------------
    // Candidate Users / Groups
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Add candidate user",
            description = "Adds a candidate user to a task"
    )
    @ApiResponse(responseCode = "200", description = "Candidate user added successfully")
    @PostMapping("/{taskId}/candidate-user/{candidateUser}")
    public ResponseEntity<String> addCandidateUser(
            @PathVariable("taskId") String taskId,
            @PathVariable("candidateUser") String candidateUser) {

        return new ResponseEntity<>(
                taskInstanceService.addCandidateUser(taskId, candidateUser),
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Add candidate group",
            description = "Adds a candidate group to a task"
    )
    @ApiResponse(responseCode = "200", description = "Candidate group added successfully")
    @PostMapping("/{taskId}/candidate-group/{candidateGroup}")
    public ResponseEntity<String> addCandidateGroup(
            @PathVariable("taskId") String taskId,
            @PathVariable("candidateGroup") String candidateGroup) {

        return new ResponseEntity<>(
                taskInstanceService.addCandidateGroup(taskId, candidateGroup),
                HttpStatus.OK
        );
    }
}
