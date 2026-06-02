package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.CategoryDto;
import com.rutusoft.flowable.dto.SectorDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto);
    List<CategoryDto> listAllCategories();
    List<CategoryDto> listAllCategoriesBySubSectorCode(String subSectorCode);
    List<CategoryDto> listAllCategoriesBySubSectorId(Long subSectorId);
    CategoryDto updateCategory(CategoryDto categoryDto);
    void deleteCategory(Long id);
}
