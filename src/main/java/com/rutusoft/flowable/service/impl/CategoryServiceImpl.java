package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.CategoryDto;
import com.rutusoft.flowable.dto.SectorDto;
import com.rutusoft.flowable.dto.SubsectorDto;
import com.rutusoft.flowable.entity.Category;
import com.rutusoft.flowable.entity.SubSector;
import com.rutusoft.flowable.exception.ValidationException;
import com.rutusoft.flowable.repository.CategoryRepository;
import com.rutusoft.flowable.repository.SubSectorRepository;
import com.rutusoft.flowable.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final SubSectorRepository subSectorRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {

        log.info("Creating category : {}", categoryDto.getCode());

        categoryRepository.findByCode(categoryDto.getCode())
                .ifPresent(category -> {

                    log.error("Category with code {} already exists",
                            categoryDto.getCode());

                    throw new ValidationException(
                            "Category already exists : " + categoryDto.getCode()
                    );
                });

        SubSector subSector = subSectorRepository
                .findById(categoryDto.getSubSectorId())
                .orElseThrow(() -> {

                    log.error("Sub sector with id {} does not exist",
                            categoryDto.getSubSectorId());

                    return new ValidationException(
                            "Sub sector does not exist : "
                                    + categoryDto.getSubSectorId()
                    );
                });

        Category category = new Category();

        category.setCode(categoryDto.getCode());
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setSubSector(subSector);

        category = categoryRepository.save(category);

        log.info("Category {} created successfully",
                category.getName());

        return mapToResponse(category);
    }

    @Override
    public List<CategoryDto> listAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CategoryDto> listAllCategoriesBySubSectorCode(String subSectorCode) {
        List<Category> categories = categoryRepository.findBySubSectorCode(subSectorCode);
        return categories.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CategoryDto> listAllCategoriesBySubSectorId(Long subSectorId) {
        List<Category> categories = categoryRepository.findBySubSectorId(subSectorId);
        return categories.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        return null;
    }

    @Override
    public void deleteCategory(Long id) {

    }

    // MAPPER
    private CategoryDto mapToResponse(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setCode(category.getCode());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        categoryDto.setSubSectorId(category.getSubSector().getId());
        return categoryDto;
    }
}
