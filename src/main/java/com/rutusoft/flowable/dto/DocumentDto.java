package com.rutusoft.flowable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {
    private Long id;
    private Long fileId;
    private String documentName;
    private String documentType;
    private String description;
    private LocalDateTime createdAt;
    private Long version;
    private Boolean isActive;
    private String uploadedBy;
    private Long pageId;
    private Boolean isOcrPerformed;
    private Map<String, String> metadata;
}