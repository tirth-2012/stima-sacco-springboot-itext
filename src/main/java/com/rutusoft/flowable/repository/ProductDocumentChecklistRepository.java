package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.ProductDocumentChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductDocumentChecklistRepository
        extends JpaRepository<ProductDocumentChecklist, Long> {

    List<ProductDocumentChecklist>
    findByProductNameOrderByIsMandatoryDesc(String productName);

    Page<ProductDocumentChecklist>
    findByProductNameOrderByIsMandatoryDesc(
            String productName,
            Pageable pageable
    );

    @Query("SELECT COALESCE(MAX(p.documentTypeId), 0) FROM ProductDocumentChecklist p")
    Integer findMaxDocumentTypeId();
}