package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.ProductRequestDto;
import com.rutusoft.flowable.dto.ProductResponseDto;
import com.rutusoft.flowable.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Tag(
        name = "Product APIs",
        description = "APIs for managing products (CRUD operations)"
)
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ------------------------------------------------------------------------
    // Create Product
    // ------------------------------------------------------------------------

    @Operation(
            summary = "Create a new product",
            description = "Creates a product with product details"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Product created successfully",
            content = @Content(
                    schema = @Schema(implementation = ProductResponseDto.class)
            )
    )
    @ApiResponse(responseCode = "400", description = "Invalid product data")
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto requestDto) {

        return new ResponseEntity<>(
                productService.createProduct(requestDto),
                HttpStatus.CREATED
        );
    }

    // ------------------------------------------------------------------------
    // Get All Products
    // ------------------------------------------------------------------------

    @Operation(
            summary = "Get all products",
            description = "Returns list of all products"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Products retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // ------------------------------------------------------------------------
    // Get Product By ID
    // ------------------------------------------------------------------------

    @Operation(
            summary = "Get product by ID",
            description = "Returns product details for given product ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product retrieved successfully"
    )
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @Parameter(description = "Product ID", example = "1", required = true)
            @PathVariable Long id) {

        return ResponseEntity.ok(productService.getProductById(id));
    }

    // ------------------------------------------------------------------------
    // Update Product
    // ------------------------------------------------------------------------

    @Operation(
            summary = "Update product",
            description = "Updates product details"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product updated successfully"
    )
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @Parameter(description = "Product ID", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto requestDto) {

        return ResponseEntity.ok(
                productService.updateProduct(id, requestDto)
        );
    }

    // ------------------------------------------------------------------------
    // Delete Product
    // ------------------------------------------------------------------------

    @Operation(
            summary = "Delete product",
            description = "Deletes product by ID"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Product deleted successfully"
    )
    @ApiResponse(responseCode = "404", description = "Product not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @Parameter(description = "Product ID", example = "1", required = true)
            @PathVariable Long id) {

        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    // ------------------------------------------------------------------------
    // Grouping Product
    // ------------------------------------------------------------------------

    @GetMapping("/grouped-by-type")
    public ResponseEntity<Map<String, List<ProductResponseDto>>> getGroupedProducts() {
        return ResponseEntity.ok(productService.getProductsGroupedByType());
    }

    // ------------------------------------------------------------------------
    // Get 15 Products
    // ------------------------------------------------------------------------
    @GetMapping("/paginated")
    public ResponseEntity<?> getProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(
                productService.getProductsPaginated(page, size, sortBy, sortDir)
        );
    }

    // ------------------------------------------------------------------------
    // Get Product Names By Product Type
    // ------------------------------------------------------------------------

    @GetMapping("/product-names")
    public ResponseEntity<List<String>> getProductNamesByType(
            @RequestParam String productType) {

        return ResponseEntity.ok(
                productService.getProductNamesByProductType(productType)
        );
    }
}
