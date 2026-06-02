package com.rutusoft.flowable.dto;
import lombok.Data;

@Data
public class MetadataRequestDTO {
    private String processInstanceId;
    private String status;
    private String uploadedBy;
}
