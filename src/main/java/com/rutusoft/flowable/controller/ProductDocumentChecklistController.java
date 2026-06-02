package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.entity.ProductDocumentChecklist;
import com.rutusoft.flowable.service.ProductDocumentChecklistService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-checklist")
public class ProductDocumentChecklistController {

    private final ProductDocumentChecklistService service;

    public ProductDocumentChecklistController(ProductDocumentChecklistService service) {
        this.service = service;
    }

    @PostMapping
    public ProductDocumentChecklist create(@RequestBody ProductDocumentChecklist checklist) {
        return service.create(checklist);
    }

    @GetMapping
    public List<ProductDocumentChecklist> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ProductDocumentChecklist getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ProductDocumentChecklist update(
            @PathVariable Long id,
            @RequestBody ProductDocumentChecklist checklist) {
        return service.update(id, checklist);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // 🔥 MAIN API for your checklist UI
    @GetMapping("/product/{productName}")
    public List<ProductDocumentChecklist> getChecklistByProduct(
            @PathVariable String productName) {
        return service.getChecklistByProduct(productName);
    }

    @GetMapping("/product/{productName}/paginated")
    public Page<ProductDocumentChecklist> getChecklistByProductPaginated(
            @PathVariable String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return service.getChecklistByProductPaginated(productName, page, size);
    }
}