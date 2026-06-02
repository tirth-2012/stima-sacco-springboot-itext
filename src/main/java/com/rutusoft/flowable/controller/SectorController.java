package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.ProductRequestDto;
import com.rutusoft.flowable.dto.ProductResponseDto;
import com.rutusoft.flowable.dto.SectorDto;
import com.rutusoft.flowable.service.SectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(
        name = "Sector APIs",
        description = "APIs for managing Sector (CRUD operations)"
)
@RestController
@RequestMapping("/sectors")
@RequiredArgsConstructor
public class SectorController {
    private final SectorService sectorService;

    @Operation(
            summary = "Create a new sector",
            description = "Creates a new sector with sector details"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Sector created successfully",
            content = @Content(
                    schema = @Schema(implementation = SectorDto.class)
            )
    )
    @ApiResponse(responseCode = "400", description = "Invalid sector data")
    @PostMapping
    public ResponseEntity<SectorDto> createSector(
            @Valid @RequestBody SectorDto sectorDto) {

        return new ResponseEntity<>(
                sectorService.createSector(sectorDto),
                HttpStatus.CREATED
        );
    }

    @Operation(
            summary = "Get all sectors",
            description = "Returns list of all sectors"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Sectors retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<SectorDto>> getAllSectors() {
        return ResponseEntity.ok(sectorService.listAllSectors());
    }

}