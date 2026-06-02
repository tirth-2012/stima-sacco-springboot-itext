package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.ChecklistDTO;
import com.rutusoft.flowable.entity.Checklist;

import java.util.List;

public interface ChecklistService {

    ChecklistDTO getChecklist(String productName, String productType, String stage);

    Checklist addChecklist(Checklist checklist);

    List<Checklist> getAllChecklists();

    Checklist updateChecklist(Long id, Checklist checklist);

    void deleteChecklist(Long id);
}