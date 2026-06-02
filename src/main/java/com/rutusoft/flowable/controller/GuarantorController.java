package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.GuarantorRequestDto;
import com.rutusoft.flowable.dto.GuarantorResponseDto;
import com.rutusoft.flowable.service.GuarantorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(
        name = "Guarantor APIs",
        description = "APIs for managing guarantors"
)
@RestController
@RequestMapping("/guarantors")
@RequiredArgsConstructor
public class GuarantorController {
    private final GuarantorService guarantorService;

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------
    @Operation(summary = "Create Guarantor")
    @PostMapping
    public ResponseEntity<GuarantorResponseDto> createGuarantor(@Valid @RequestBody GuarantorRequestDto dto) {
        return new ResponseEntity<>(guarantorService.createGuarantor(dto), HttpStatus.CREATED);
    }

    // ----------------------------------------------------------------
    // GET ALL
    // ----------------------------------------------------------------
    @Operation(summary = "Get All Guarantors")
    @GetMapping
    public ResponseEntity<Page<GuarantorResponseDto>> getAllGuarantors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                guarantorService.getAllGuarantors(page, size)
        );
    }

    // ----------------------------------------------------------------
    // GET BY ID
    // ----------------------------------------------------------------
    @Operation(summary = "Get Guarantor By ID")
    @GetMapping("/{id}")
    public ResponseEntity<GuarantorResponseDto> getGuarantorById(@PathVariable Long id) {
        return ResponseEntity.ok(guarantorService.getGuarantorById(id));
    }

    // ----------------------------------------------------------------
    // GET BY Process Instance ID
    // ----------------------------------------------------------------
    @Operation(summary = "Get Guarantors By Process Instance Id")
    @GetMapping("/process-instances/{processInstanceId}")
    public ResponseEntity<List<GuarantorResponseDto>> getGuarantorById(@PathVariable String processInstanceId) {
        return ResponseEntity.ok(guarantorService.getGuarantorsByProcessInstanceId(processInstanceId));
    }

    // ----------------------------------------------------------------
    // GET active consents
    // ----------------------------------------------------------------
    @Operation(summary = "Get my active consents")
    @GetMapping("/my-active-consents")
    public ResponseEntity<List<GuarantorResponseDto>> getMyActiveConsents() {
        return ResponseEntity.ok(guarantorService.getMyActiveConsents());
    }

    @Operation(summary = "Get my active consents count")
    @GetMapping("/my-active-consents/count")
    public ResponseEntity<Long> getMyActiveConsentsCount() {

        return ResponseEntity.ok(
                guarantorService.getMyActiveConsentsCount()
        );
    }

    @Operation(summary = "Get my historical consents")
    @GetMapping("/my-historical-consents")
    public ResponseEntity<List<GuarantorResponseDto>> getMyHistoricalConsents() {
        return ResponseEntity.ok(guarantorService.getMyHistoricalConsents());
    }

    @Operation(summary = "Get my guarantor requests")
    @GetMapping("/my-guarantor-requests")
    public ResponseEntity<List<GuarantorResponseDto>>
    getMyGuarantorRequests() {

        return ResponseEntity.ok(
                guarantorService.getMyGuarantorRequests()
        );
    }

    @Operation(summary = "Get my existing guarantees count")
    @GetMapping("/my-existing-guarantees/count")
    public ResponseEntity<Long>
    getMyExistingGuaranteesCount() {

        return ResponseEntity.ok(
                guarantorService
                        .getMyExistingGuaranteesCount()
        );
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------
    @Operation(summary = "Update Guarantor")
    @PutMapping("/{id}")
    public ResponseEntity<GuarantorResponseDto> updateGuarantor(@PathVariable Long id, @Valid @RequestBody GuarantorRequestDto dto) {
        return ResponseEntity.ok(guarantorService.updateGuarantor(id, dto));
    }


    @Operation(summary = "Update Guarantor status")
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateGuarantor(@PathVariable Long id, @RequestParam("status") String status) {
        return ResponseEntity.ok(guarantorService.updateStatus(id, status));
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------
    @Operation(summary = "Delete Guarantor")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGuarantor(@PathVariable Long id) {
        guarantorService.deleteGuarantor(id);
        return ResponseEntity.ok("Guarantor deleted successfully");
    }
}