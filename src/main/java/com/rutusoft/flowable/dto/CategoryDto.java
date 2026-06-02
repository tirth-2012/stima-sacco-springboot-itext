package com.rutusoft.flowable.dto;

import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long subSectorId;
}
