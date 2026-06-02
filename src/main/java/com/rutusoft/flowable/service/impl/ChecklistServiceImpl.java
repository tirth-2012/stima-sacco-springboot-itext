package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.ChecklistDTO;
import com.rutusoft.flowable.entity.Checklist;
import com.rutusoft.flowable.repository.ChecklistRepository;
import com.rutusoft.flowable.service.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {

    private final ChecklistRepository checklistRepository;

    // ================================
    // EXISTING LOGIC
    // ================================
    @Override
    public ChecklistDTO getChecklist(
            String productName,
            String productType,
            String stage
    ) {

        List<Checklist> list;

        if (productType == null || productType.isBlank()) {

            list = checklistRepository.findChecklistWithoutProductType(
                    productName,
                    stage
            );

        } else {

            list = checklistRepository.findChecklist(
                    productName,
                    productType,
                    stage
            );
        }

        if (list.isEmpty()) {
            return new ChecklistDTO(0, new ArrayList<>());
        }

        int totalItems = list.size();

        Map<String, List<Checklist>> grouped =
                list.stream().collect(Collectors.groupingBy(
                        Checklist::getCategory,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<ChecklistDTO.Category> categories = grouped.entrySet().stream()
                .map(entry -> {

                    List<ChecklistDTO.Item> items = entry.getValue().stream()
                            .sorted(Comparator.comparing(Checklist::getChecklistOrder))
                            .map(c -> new ChecklistDTO.Item(
                                    c.getId(),
                                    c.getChecklistTitle(),
                                    c.getIsMandatory(),
                                    c.getVariable()
                            ))
                            .toList();

                    return new ChecklistDTO.Category(entry.getKey(), items);
                })
                .toList();

        return new ChecklistDTO(totalItems, categories);
    }

    // ================================
    // ADD CHECKLIST
    // ================================
    @Override
    public Checklist addChecklist(Checklist checklist) {
        return checklistRepository.save(checklist);
    }

    // ================================
    // GET ALL CHECKLIST
    // ================================
    @Override
    public List<Checklist> getAllChecklists() {
        return checklistRepository.findAll(
                Sort.by("categoryOrder").ascending()
                        .and(Sort.by("checklistOrder").ascending())
        );
    }

    // ================================
    //  UPDATE CHECKLIST
    // ================================
    @Override
    public Checklist updateChecklist(Long id, Checklist updatedChecklist) {

        Checklist existing = checklistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Checklist not found with id: " + id));

        //  Update fields
        existing.setChecklistTitle(updatedChecklist.getChecklistTitle());
        existing.setCategory(updatedChecklist.getCategory());
        existing.setCategoryOrder(updatedChecklist.getCategoryOrder());
        existing.setChecklistOrder(updatedChecklist.getChecklistOrder());
        existing.setProductName(updatedChecklist.getProductName());
        existing.setProductType(updatedChecklist.getProductType());
        existing.setStage(updatedChecklist.getStage());
        existing.setIsMandatory(updatedChecklist.getIsMandatory());
        existing.setVariable(updatedChecklist.getVariable());

        return checklistRepository.save(existing);
    }

    // ================================
    //  DELETE CHECKLIST
    // ================================
    @Override
    public void deleteChecklist(Long id) {
        if (!checklistRepository.existsById(id)) {
            throw new RuntimeException("Checklist not found with id: " + id);
        }
        checklistRepository.deleteById(id);
    }
}