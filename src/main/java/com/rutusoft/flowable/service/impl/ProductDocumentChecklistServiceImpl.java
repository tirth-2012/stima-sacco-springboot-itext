package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.entity.ProductDocumentChecklist;
import com.rutusoft.flowable.repository.ProductDocumentChecklistRepository;
import com.rutusoft.flowable.service.DocumentService;
import com.rutusoft.flowable.service.ProductDocumentChecklistService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ProductDocumentChecklistServiceImpl
        implements ProductDocumentChecklistService {

    private final ProductDocumentChecklistRepository repository;
    private final DocumentService documentService;

    public ProductDocumentChecklistServiceImpl(ProductDocumentChecklistRepository repository, DocumentService documentService) {
        this.repository = repository;
        this.documentService = documentService;
    }

    @Override
    public ProductDocumentChecklist create(ProductDocumentChecklist checklist) {
        log.info("Creating document type checklist : {}", checklist);
        if (checklist.getIsActive() == null) {
            checklist.setIsActive(true);
        }

        //Integer maxId = repository.findMaxDocumentTypeId();
        //checklist.setDocumentTypeId(checklist.getDocumentTypeId());

        return repository.save(checklist);
    }

    @Override
    public List<ProductDocumentChecklist> getAll() {
        return repository.findAll();
    }

    @Override
    public ProductDocumentChecklist getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Checklist not found"));
    }

    @Override
    public ProductDocumentChecklist update(Long id, ProductDocumentChecklist updated) {
        ProductDocumentChecklist existing = getById(id);

        existing.setProductType(updated.getProductType());
        existing.setProductName(updated.getProductName());
        existing.setDocumentType(updated.getDocumentType());
        existing.setIsMandatory(updated.getIsMandatory());
        existing.setIsActive(updated.getIsActive());

        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Checklist not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<ProductDocumentChecklist> getChecklistByProduct(String productName) {

        log.info("Fetching document types for product : {}", productName);

        return repository.findByProductNameOrderByIsMandatoryDesc(productName);
    }

    private int getMayanDocumentId(String documentType, Map<String, Object> mayanDocumentTypes) {
        List<Map<String, Object>> results =
                (List<Map<String, Object>>) mayanDocumentTypes.get("results");

        return results.stream()
                .filter(doc -> documentType.equalsIgnoreCase((String) doc.get("label")))
                .map(doc -> ((Number) doc.get("id")).intValue())
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Document type not found: " + documentType));
    }

    @Override
    public Page<ProductDocumentChecklist> getChecklistByProductPaginated(
            String productName,
            int page,
            int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);

        Pageable pageable = PageRequest.of(safePage, safeSize);

        return repository.findByProductNameOrderByIsMandatoryDesc(productName, pageable);
    }
}