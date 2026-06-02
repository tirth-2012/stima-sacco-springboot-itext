package com.rutusoft.flowable.dto;

import lombok.Data;

@Data
public class SubsectorDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Long sectorId;
}
