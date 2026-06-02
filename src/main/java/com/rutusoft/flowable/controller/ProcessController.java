package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.ProcessInstancesResponseDto;
import com.rutusoft.flowable.service.ProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/process-instances")
@Tag(
        name = "Process Instance APIs",
        description = "APIs for starting and querying Flowable process instances"
)
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @Operation(
            summary = "Start a new process instance",
            description = "Starts a Flowable process instance using the given process definition key and initial variables"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Process instance started successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "2501")
            )
    )
    @ApiResponse(responseCode = "400", description = "Invalid process definition key or variables")
    @ApiResponse(responseCode = "404", description = "Process definition not found")
    @PostMapping("/start/{processDefinitionKey}")
    public ResponseEntity<String> startProcess(
            @Parameter(
                    description = "Process definition key",
                    required = true,
                    example = "loanApprovalProcess"
            )
            @PathVariable("processDefinitionKey")
            @NotBlank(message = "processDefinitionKey must not be blank")
            String processDefinitionKey,

            @Parameter(
                    description = "Initial process variables as key-value pairs",
                    required = true,
                    schema = @Schema(
                            example = "{\"customerName\":\"Sumit Vadaviya\",\"loanAmount\":70000,\"priority\":true}"
                    )
            )
            @RequestBody
            Map<String, Object> processVariables) {

        String processInstanceId = processService.startProcess(processDefinitionKey, processVariables);
        return ResponseEntity.status(HttpStatus.CREATED).body(processInstanceId);
    }

    @Operation(
            summary = "Get active process instances",
            description = "Returns a paginated list of active Flowable process instances"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Active process instances retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProcessInstancesResponseDto.class))
    )
    @GetMapping("/active")
    public ResponseEntity<ProcessInstancesResponseDto> getActiveProcessInstances(
            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "from must be 0 or greater")
            int from,

            @Parameter(description = "Maximum number of records to fetch", example = "10")
            @RequestParam(value = "to", defaultValue = "10")
            @Min(value = 1, message = "to must be 1 or greater")
            int to) {

        ProcessInstancesResponseDto response = processService.activeProcessInstances(from, to);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get active process instances by initiator",
            description = "Returns a paginated list of active Flowable process instances filtered by initiator"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Active process instances retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProcessInstancesResponseDto.class))
    )
    @GetMapping("/active/initiator/{initiator}")
    public ResponseEntity<ProcessInstancesResponseDto> getActiveProcessInstancesByInitiator(
            @Parameter(description = "Process initiator", example = "admin")
            @PathVariable("initiator")
            @NotBlank(message = "initiator must not be blank")
            String initiator,

            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "from must be 0 or greater")
            int from,

            @Parameter(description = "Maximum number of records to fetch", example = "10")
            @RequestParam(value = "to", defaultValue = "10")
            @Min(value = 1, message = "to must be 1 or greater")
            int to) {

        ProcessInstancesResponseDto response =
                processService.activeProcessInstancesByInitiator(initiator, from, to);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get process instances by process definition key",
            description = "Returns a paginated list of process instances for a given process definition key"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Process instances retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProcessInstancesResponseDto.class))
    )
    @ApiResponse(responseCode = "404", description = "Process definition not found")
    @GetMapping("/{processDefinitionKey}")
    public ResponseEntity<ProcessInstancesResponseDto> getProcessInstancesByDefinitionKey(
            @Parameter(
                    description = "Process definition key",
                    required = true,
                    example = "loanApprovalProcess"
            )
            @PathVariable("processDefinitionKey")
            @NotBlank(message = "processDefinitionKey must not be blank")
            String processDefinitionKey,

            @Parameter(description = "Starting index for pagination", example = "0")
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "from must be 0 or greater")
            int from,

            @Parameter(description = "Maximum number of records to fetch", example = "10")
            @RequestParam(value = "to", defaultValue = "10")
            @Min(value = 1, message = "to must be 1 or greater")
            int to) {

        ProcessInstancesResponseDto response =
                processService.listProcessInstancesByKey(processDefinitionKey, from, to);

        return ResponseEntity.ok(response);
    }

    @ApiResponse(responseCode = "404", description = "Process definition not found")
    @GetMapping("/recent")
    public ResponseEntity<ProcessInstancesResponseDto> getRecentProcessInstances(@RequestParam(name = "segment", required = false) String segment,
                                                                                 @RequestParam(name = "referenceId", required = false) String referenceId
           ) {

        ProcessInstancesResponseDto response =
                processService.getRecentProcessInstances(segment, referenceId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Modify process instance",
            description = "Modify process instance"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Process instance modified successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(example = "2501")
            )
    )
    @ApiResponse(responseCode = "422", description = "Invalid process instance Id")
    @ApiResponse(responseCode = "404", description = "Process instance not found")
    @PostMapping("/modify/{processInstanceId}")
    public ResponseEntity<String> modifyProcessInstance(
            @Parameter(
                    description = "Process instance id",
                    required = true,
                    example = "1"
            )
            @PathVariable("processInstanceId")
            @NotBlank(message = "processInstanceId must not be blank")
            String processInstanceId,

            @Parameter(description = "Current activity Id", example = "0")
            @RequestParam(value = "currentActivityId", defaultValue = "0")
            String currentActivityId,

            @Parameter(description = "New activity Id", example = "0")
            @RequestParam(value = "newActivityId", defaultValue = "0")
            String newActivityId,

            @Parameter(
                    description = "Send back payload",
                    required = true,
                    schema = @Schema(
                            example = "{\"action\":\"Sent back\",\"action_reason\":\"Need to upload correct ID proof\",\"action_by\":\"akapoor\",\"stepBackTo\":\"usertask_branch_manager\"}"
                    )
            )
            @RequestBody
            Map<String, Object> variables
            ) {
        return ResponseEntity.ok(processService.modifyProcessInstanceState(processInstanceId, currentActivityId, newActivityId, variables));
    }

    @GetMapping(value = "/process-diagram/{processInstanceId}", produces = MediaType.IMAGE_PNG_VALUE)
    public void getProcessDiagram(@PathVariable String processInstanceId, HttpServletResponse response) throws IOException {
        processService.getProcessDiagram(processInstanceId, response);
    }

    @GetMapping(value = "/pipelines/{processDefinitionKey}")
    public ResponseEntity<Object> getApplicationPipeline(@PathVariable("processDefinitionKey") String processDefinitionKey) throws IOException {
        return ResponseEntity.ok(processService.getActiveApplicationsGroupedByTask(processDefinitionKey));
    }

    @PostMapping("/terminate/{processInstanceId}")
    public ResponseEntity<String> terminateProcessInstance(
            @Parameter(
                    description = "Process instance id",
                    required = true,
                    example = "1"
            )
            @PathVariable("processInstanceId")
            @NotBlank(message = "processInstanceId must not be blank")
            String processInstanceId,

            @Parameter(description = "Termination reason", example = "Credit score not is good")
            @NotBlank(message = "Reason must not be blank")
            @RequestParam(value = "reason")
            String reason){
        return ResponseEntity.ok(processService.terminateProcessInstance(processInstanceId, reason));
    }
}