package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.CategoryDto;
import com.rutusoft.flowable.dto.SubsectorDto;
import com.rutusoft.flowable.service.CategoryService;
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
        name = "Category APIs",
        description = "APIs for managing Categories (CRUD operations)"
)
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(
            summary = "Get all categories",
            description = "Returns list of all categories"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Categories retrieved successfully"
    )
    @GetMapping("/")
    public ResponseEntity<List<CategoryDto>> getAllCategires() {
        return ResponseEntity.ok(categoryService.listAllCategories());
    }

    @Operation(
            summary = "Get all categories by sub sector code",
            description = "Returns list of all categories by sub sector code"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Categories retrieved successfully"
    )
    @GetMapping("/sub-sector/code/{subSectorCode}")
    public ResponseEntity<List<CategoryDto>> getAllCategoriesBySubsectorCode(@PathVariable("subSectorCode") String subSectorCode) {
        return ResponseEntity.ok(categoryService.listAllCategoriesBySubSectorCode(subSectorCode));
    }

    @Operation(
            summary = "Get all categories by sub sector code",
            description = "Returns list of all categories by sub sector code"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Categories retrieved successfully"
    )
    @GetMapping("/sub-sector/id/{subSectorId}")
    public ResponseEntity<List<CategoryDto>> getAllCategoriesBySubsectorId(@PathVariable("subSectorId") Long subSectorId) {
        return ResponseEntity.ok(categoryService.listAllCategoriesBySubSectorId(subSectorId));
    }
}
