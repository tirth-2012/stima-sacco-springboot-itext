package com.rutusoft.flowable.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDto {
    private String productCode;
    private String description;
    private Integer quantity;
    private Double price;
    private Double totalPrice;
}
