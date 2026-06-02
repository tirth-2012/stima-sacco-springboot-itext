package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.Checklist;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    @Query("""
        SELECT c FROM Checklist c
        WHERE LOWER(c.productName) = LOWER(:productName)
        AND LOWER(c.productType) = LOWER(:productType)
        AND LOWER(c.stage) = LOWER(:stage)
        ORDER BY c.categoryOrder, c.checklistOrder
    """)
    List<Checklist> findChecklist(
            @Param("productName") String productName,
            @Param("productType") String productType,
            @Param("stage") String stage
    );

    @Query("""
        SELECT c FROM Checklist c
        WHERE LOWER(c.productName) = LOWER(:productName)
        AND c.productType IS NULL
        AND LOWER(c.stage) = LOWER(:stage)
        ORDER BY c.categoryOrder, c.checklistOrder
    """)
    List<Checklist> findChecklistWithoutProductType(
            @Param("productName") String productName,
            @Param("stage") String stage
    );
}