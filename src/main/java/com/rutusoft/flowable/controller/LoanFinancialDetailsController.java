package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.LoanFinancialDetailsRequestDto;
import com.rutusoft.flowable.dto.LoanFinancialDetailsResponseDto;
import com.rutusoft.flowable.service.LoanFinancialDetailsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(
        name = "Loan Financial Details APIs",
        description = "APIs for managing loan financial details"
)
@RestController
@RequestMapping("/loan-financial-details")
@RequiredArgsConstructor
public class LoanFinancialDetailsController {
    private final LoanFinancialDetailsService service;

    // ------------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------------
    @Operation(summary = "Create Loan Financial Details")
    @PostMapping
    public ResponseEntity<LoanFinancialDetailsResponseDto>
    createFinancialDetails(
            @Valid @RequestBody LoanFinancialDetailsRequestDto dto
    ) {

        return new ResponseEntity<>(
                service.createFinancialDetails(dto),
                HttpStatus.CREATED
        );
    }

    // ------------------------------------------------------------------------
    // GET ALL
    // ------------------------------------------------------------------------
    @Operation(summary = "Get All Loan Financial Details")
    @GetMapping
    public ResponseEntity<Page<LoanFinancialDetailsResponseDto>>
    getAllFinancialDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                service.getAllFinancialDetails(page, size)
        );
    }

    // ------------------------------------------------------------------------
    // GET BY ID
    // ------------------------------------------------------------------------
    @Operation(summary = "Get Loan Financial Details By ID")
    @GetMapping("/{id}")
    public ResponseEntity<LoanFinancialDetailsResponseDto>
    getFinancialDetailsById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.getFinancialDetailsById(id)
        );
    }

    // ------------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------------
    @Operation(summary = "Update Loan Financial Details")
    @PutMapping("/{id}")
    public ResponseEntity<LoanFinancialDetailsResponseDto>
    updateFinancialDetails(
            @PathVariable Long id,
            @Valid @RequestBody LoanFinancialDetailsRequestDto dto
    ) {

        return ResponseEntity.ok(
                service.updateFinancialDetails(id, dto)
        );
    }

    // ------------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------------
    @Operation(summary = "Delete Loan Financial Details")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFinancialDetails(
            @PathVariable Long id
    ) {

        service.deleteFinancialDetails(id);

        return ResponseEntity.ok(
                "Loan financial details deleted successfully"
        );
    }
}