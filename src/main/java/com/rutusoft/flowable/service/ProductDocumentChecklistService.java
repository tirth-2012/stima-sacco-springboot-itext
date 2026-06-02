package com.rutusoft.flowable.service;

import com.rutusoft.flowable.entity.ProductDocumentChecklist;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProductDocumentChecklistService {

    ProductDocumentChecklist create(ProductDocumentChecklist checklist);

    List<ProductDocumentChecklist> getAll();

    ProductDocumentChecklist getById(Long id);
    void delete(Long id);
    ProductDocumentChecklist update(Long id, ProductDocumentChecklist checklist);

    List<ProductDocumentChecklist> getChecklistByProduct(String productName);

    Page<ProductDocumentChecklist> getChecklistByProductPaginated(
            String productName,
            int page,
            int size
    );
}