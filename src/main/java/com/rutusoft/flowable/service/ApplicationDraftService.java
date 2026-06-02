package com.rutusoft.flowable.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rutusoft.flowable.dto.ApplicationDraftDto;
import com.rutusoft.flowable.entity.ApplicationDraft;
import com.rutusoft.flowable.repository.ApplicationDraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ApplicationDraftService {

    @Autowired
    private ApplicationDraftRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    public ApplicationDraft saveDraft(ApplicationDraftDto dto) {
        try {

            ApplicationDraft draft;

            // 1. GET OR CREATE DRAFT
            if (dto.getDraftId() != null) {
                draft = repository.findByDraftId(dto.getDraftId())
                        .orElseThrow(() -> new RuntimeException("Draft not found"));
            } else {
                draft = new ApplicationDraft();
                draft.setDraftId(UUID.randomUUID().toString());
                draft.setUserId(dto.getUserId());
                draft.setStatus("DRAFT");
                draft.setCreatedAt(new Date());
            }

            draft.setCurrentStep(dto.getStep());
            draft.setUpdatedAt(new Date());

            // 2. EXISTING DATA
            Map<String, Object> existingData = draft.getFormData() != null
                    ? objectMapper.convertValue(draft.getFormData(), Map.class)
                    : new java.util.HashMap<>();

            // 3. NEW DATA FROM FRONTEND
            Map<String, Object> newData = dto.getFormData();

            if (newData == null) {
                draft.setFormData(existingData);
                return repository.save(draft);
            }

            // 4. HANDLE CUSTOMER (STEP 1)
            if (newData.containsKey("customer")) {
                existingData.put("customer", newData.get("customer"));
            }

            // 5. HANDLE PRODUCT (STEP 2)
            if (newData.containsKey("formData")) {

                Map<String, Object> stepData =
                        (Map<String, Object>) newData.get("formData");

                if (stepData != null) {

                    String productName = (String) stepData.get("selectedProduct");

                    if (productName != null) {

                        // remove name from fields
                        stepData.remove("selectedProduct");

                        Map<String, Object> productWrapper = new java.util.HashMap<>();
                        productWrapper.put("name", productName);
                        productWrapper.putAll(stepData);

                        existingData.put("selectedProduct", productWrapper);
                    }
                }
            }

            //  6. HANDLE FINANCIAL DATA (STEP 3)
            if (newData.containsKey("financialData")) {
                existingData.put("financialData", newData.get("financialData"));
            }

            // 7. HANDLE SECURITY DATA
            if (newData.containsKey("securityData")) {
                existingData.put("securityData", newData.get("securityData"));
            }

            // 8. HANDLE DOCUMENTS
            if (newData.containsKey("documents")) {
                existingData.put("documents", newData.get("documents"));
            }

            // 9. SAVE FINAL MERGED DATA
            draft.setFormData(existingData);

            return repository.save(draft);

        } catch (Exception e) {
            throw new RuntimeException("Error saving draft", e);
        }
    }

    public ApplicationDraft getDraftByUser(String userId) {
        return repository.findByUserIdAndStatus(userId, "DRAFT")
                .orElse(null);
    }

    public void deleteDraft(String draftId) {
        repository.findByDraftId(draftId)
                .ifPresent(repository::delete);
    }

    public void markSubmitted(String draftId) {
        ApplicationDraft draft = repository.findByDraftId(draftId)
                .orElseThrow(() -> new RuntimeException("Draft not found"));

        draft.setStatus("SUBMITTED");
        repository.save(draft);
    }

    public List<ApplicationDraft> getAllDrafts(String userId) {
        return repository.findAllByUserIdAndStatus(userId, "DRAFT");
    }
}