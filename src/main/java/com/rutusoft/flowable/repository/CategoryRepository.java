package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findBySubSectorId(Long subSectorId);
    Optional<Category> findByCode(String code);
    List<Category> findBySubSectorCode(String subSectorCode);
}