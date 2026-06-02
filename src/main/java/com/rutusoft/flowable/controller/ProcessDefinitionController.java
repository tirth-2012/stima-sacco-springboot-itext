package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.ProcessDefinitionDto;
import com.rutusoft.flowable.service.ProcessDefinitionService;
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

@Tag(
        name = "Process Definition APIs",
        description = "APIs for querying Flowable process definitions"
)
@RestController
@RequestMapping("/process-definitions")
public class ProcessDefinitionController {

    @Autowired
    private ProcessDefinitionService processDefinitionService;

    // ------------------------------------------------------------------------
    // Latest Process Definitions
    // ------------------------------------------------------------------------

    @Operation(
            summary = "Get latest process definitions",
            description = "Returns the latest version of each deployed Flowable process definition"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Latest process definitions retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProcessDefinitionDto.class)
            )
    )
    @GetMapping("/latest")
    public ResponseEntity<List<ProcessDefinitionDto>> latestDefinitions() {
        return new ResponseEntity<>(
                processDefinitionService.getLatestDefinitions(),
                HttpStatus.OK
        );
    }

    // ------------------------------------------------------------------------
    // Active Process Definitions
    // ------------------------------------------------------------------------

    @Operation(
            summary = "Get active process definitions",
            description = "Returns all active Flowable process definitions"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Active process definitions retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProcessDefinitionDto.class)
            )
    )
    @GetMapping("/active")
    public ResponseEntity<List<ProcessDefinitionDto>> activeDefinitions() {
        return new ResponseEntity<>(
                processDefinitionService.getActiveDefinitions(),
                HttpStatus.OK
        );
    }

    // ------------------------------------------------------------------------
    // Process Definition by ID and Version
    // ------------------------------------------------------------------------

    @Operation(
            summary = "Get process definition by ID and version",
            description = "Returns a specific Flowable process definition using processDefinitionId and version number"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Process definition retrieved successfully",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProcessDefinitionDto.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Process definition not found"
    )
    @GetMapping("/active/{processDefinitionId}/{versionId}")
    public ResponseEntity<List<ProcessDefinitionDto>> getProcessDefinitionByVersion(
            @Parameter(
                    description = "Process definition ID",
                    required = true,
                    example = "loanApprovalProcess"
            )
            @PathVariable("processDefinitionId") String processDefinitionId,

            @Parameter(
                    description = "Process definition version",
                    required = true,
                    example = "1"
            )
            @PathVariable("versionId") int versionId) {

        return new ResponseEntity<>(
                processDefinitionService.getDefinition(processDefinitionId, versionId),
                HttpStatus.OK
        );
    }
}
