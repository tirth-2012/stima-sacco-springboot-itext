package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.LoanApplicationRequestDto;
import com.rutusoft.flowable.dto.LoanApplicationResponseDto;
import com.rutusoft.flowable.service.LoanApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(
        name = "Loan Application APIs",
        description = "APIs for managing loan applications"
)
@RestController
@RequestMapping("/loan-applications")
public class LoanApplicationController {

    private final LoanApplicationService service;

    public LoanApplicationController(
            LoanApplicationService service
    ) {
        this.service = service;
    }

    // ------------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------------
    @Operation(summary = "Create Loan Application")
    @PostMapping
    public ResponseEntity<LoanApplicationResponseDto> createApplication(
            @Valid @RequestBody LoanApplicationRequestDto dto
    ) {

        return new ResponseEntity<>(
                service.createApplication(dto),
                HttpStatus.CREATED
        );
    }

    // ------------------------------------------------------------------------
    // GET ALL
    // ------------------------------------------------------------------------
    @Operation(summary = "Get All Loan Applications")
    @GetMapping
    public ResponseEntity<Page<LoanApplicationResponseDto>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                service.getAllApplications(page, size)
        );
    }

    // ------------------------------------------------------------------------
    // GET BY ID
    // ------------------------------------------------------------------------
    @Operation(summary = "Get Loan Application By ID")
    @GetMapping("/{id}")
    public ResponseEntity<LoanApplicationResponseDto> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.getApplicationById(id)
        );
    }

    // ------------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------------
    @Operation(summary = "Update Loan Application")
    @PutMapping("/{id}")
    public ResponseEntity<LoanApplicationResponseDto> updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody LoanApplicationRequestDto dto
    ) {

        return ResponseEntity.ok(
                service.updateApplication(id, dto)
        );
    }

    // ------------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------------
    @Operation(summary = "Delete Loan Application")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(
            @PathVariable Long id
    ) {

        service.deleteApplication(id);

        return ResponseEntity.ok(
                "Loan application deleted successfully"
        );
    }
}