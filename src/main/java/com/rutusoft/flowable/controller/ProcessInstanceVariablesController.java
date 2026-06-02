package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.VariableInstanceDto;
import com.rutusoft.flowable.service.ProcessInstanceVariablesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
        name = "Process Instance Variable APIs",
        description = "APIs for managing Flowable process instance variables"
)
@RestController
@RequestMapping("/process-instances")
public class ProcessInstanceVariablesController {

    @Autowired
    private ProcessInstanceVariablesService processInstanceVariablesService;

    // ----------------------------------------------------------------------
    // Get Process Instance Variables
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Get process instance variables",
            description = "Retrieves all runtime variables for a given Flowable process instance"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Variables retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            description = "Key-value map of process variables",
                            example = "{\"loanAmount\": 50000, \"status\": \"APPROVED\"}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Process instance not found"
    )
    @GetMapping("/{processInstanceId}/variables")
    public ResponseEntity<List<VariableInstanceDto>> getProcessInstanceVariables(
            @Parameter(
                    description = "Flowable process instance ID",
                    required = true,
                    example = "a1b2c3d4-1234-5678-9012-abcdef123456"
            )
            @PathVariable("processInstanceId") String processInstanceId) {

        return new ResponseEntity<>(
                processInstanceVariablesService.getProcessInstanceVariables(processInstanceId),
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Get process instance variables in Map format",
            description = "Retrieves all runtime variables for a given Flowable process instance"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Variables retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            description = "Key-value map of process variables",
                            example = "{\"loanAmount\": 50000, \"status\": \"APPROVED\"}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Process instance not found"
    )
    @GetMapping("/{processInstanceId}/variables/map")
    public ResponseEntity<Map<String, Object>> getProcessInstanceVariablesMap(
            @Parameter(
                    description = "Flowable process instance ID",
                    required = true,
                    example = "a1b2c3d4-1234-5678-9012-abcdef123456"
            )
            @PathVariable("processInstanceId") String processInstanceId) {

        return new ResponseEntity<>(
                processInstanceVariablesService.getProcessInstanceVariablesMap(processInstanceId),
                HttpStatus.OK
        );
    }

    // ----------------------------------------------------------------------
    // Create or Update (Upsert) Process Instance Variables
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Create or update process instance variables",
            description = """
            Creates new variables or updates existing variables for a Flowable process instance.
            This operation is idempotent and supports bulk variable updates.
            """
    )
    @ApiResponse(
            responseCode = "201",
            description = "Variables created or updated successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid variable payload"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Process instance not found"
    )
    @PostMapping("/{processInstanceId}/variables")
    public ResponseEntity<String> upsertProcessInstanceVariables(

            @Parameter(
                    description = "Flowable process instance ID",
                    required = true,
                    example = "a1b2c3d4-1234-5678-9012-abcdef123456"
            )
            @PathVariable("processInstanceId") String processInstanceId,

            @Parameter(
                    description = "Key-value map of variables to create or update",
                    required = true,
                    schema = @Schema(
                            example = "{\"approvalStatus\": \"PENDING\", \"reviewer\": \"manager1\"}"
                    )
            )
            @RequestBody Map<String, Object> variables) {

        return new ResponseEntity<>(
                processInstanceVariablesService.upsertProcessInstanceVariables(processInstanceId, variables),
                HttpStatus.CREATED
        );
    }

    // ----------------------------------------------------------------------
    // Delete Single Process Instance Variable
    // ----------------------------------------------------------------------

    @Operation(
            summary = "Delete a process instance variable",
            description = "Deletes a specific runtime variable from a Flowable process instance"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Variable deleted successfully"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Process instance or variable not found"
    )
    @DeleteMapping("/{processInstanceId}/variables/{variableName}")
    public ResponseEntity<String> deleteProcessInstanceVariable(

            @Parameter(
                    description = "Flowable process instance ID",
                    required = true,
                    example = "a1b2c3d4-1234-5678-9012-abcdef123456"
            )
            @PathVariable("processInstanceId") String processInstanceId,

            @Parameter(
                    description = "Name of the variable to delete",
                    required = true,
                    example = "approvalStatus"
            )
            @PathVariable("variableName") String variableName) {

        return new ResponseEntity<>(
                processInstanceVariablesService.deleteProcessInstanceVariable(processInstanceId, variableName),
                HttpStatus.OK
        );
    }
}