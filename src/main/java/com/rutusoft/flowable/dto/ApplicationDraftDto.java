package com.rutusoft.flowable.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ApplicationDraftDto {

    private String userId;
    private String draftId;
    private Integer step;

    private Map<String, Object> formData;
}