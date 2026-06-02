package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.SectorDto;
import com.rutusoft.flowable.dto.SubsectorDto;
import com.rutusoft.flowable.repository.SubSectorRepository;
import com.rutusoft.flowable.service.SubsectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "Sub section APIs",
        description = "APIs for managing Sub sections (CRUD operations)"
)
@RestController
@RequestMapping("/sub-sectors")
@RequiredArgsConstructor
public class SubsectorController {
    private final SubsectorService subsectorService;

    @Operation(
            summary = "Get all sub sectors",
            description = "Returns list of all sub sectors"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sub sectors retrieved successfully"
    )
    @GetMapping("/")
    public ResponseEntity<List<SubsectorDto>> getAllSubsectors() {
        return ResponseEntity.ok(subsectorService.listAllSubsectors());
    }

    @Operation(
            summary = "Get all sub sectors by sector code",
            description = "Returns list of all sub sectors by sector code"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sub sectors retrieved successfully"
    )
    @GetMapping("/sector/code/{sectorCode}")
    public ResponseEntity<List<SubsectorDto>> getAllSubsectorsBySectorCode(@PathVariable("sectorCode") String sectorCode) {
        return ResponseEntity.ok(subsectorService.listAllSubsectorsBySectorCode(sectorCode));
    }

    @Operation(
            summary = "Get all sub sectors by sector id",
            description = "Returns list of all sub sectors by sector id"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sub sectors retrieved successfully"
    )
    @GetMapping("/sector/id/{sectorId}")
    public ResponseEntity<List<SubsectorDto>> getAllSubsectorsBySectorId(@PathVariable("sectorId") Long sectorId) {
        return ResponseEntity.ok(subsectorService.listAllSubsectorsBySectorId(sectorId));
    }


}