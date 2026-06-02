package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.ProcessInstanceAuditResponseDto;
import com.rutusoft.flowable.dto.ProcessInstancesResponseDto;
import com.rutusoft.flowable.service.ProcessInstanceAuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/audit")
public class ProcessInstanceAuditController {

    @Autowired
    private ProcessInstanceAuditService processInstanceAuditService;

    @Operation(
            summary = "Get process instance audit",
            description = "Returns a paginated list of process instance audit"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Process instance audit retrieved successfully",
            content = @Content(schema = @Schema(implementation = ProcessInstanceAuditResponseDto.class))
    )
    @GetMapping("/{processInstanceId}")
    public ResponseEntity<List<ProcessInstanceAuditResponseDto>> fetchProcessInstanceAudits(
            @Parameter(description = "Process initiator", example = "70953d43-29c9-11f1-81c1-c247406d9200")
            @PathVariable("processInstanceId")
            @NotBlank(message = "processInstanceId must not be blank")
            String processInstanceId) {
        return ResponseEntity.ok(processInstanceAuditService.fetchProcessInstanceAudits(processInstanceId));
    }
}
