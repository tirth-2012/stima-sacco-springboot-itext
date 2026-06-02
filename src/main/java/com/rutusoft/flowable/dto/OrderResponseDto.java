package com.rutusoft.flowable.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

import java.time.LocalDateTime;



@Getter
@Setter
public class OrderResponseDto {

    private Long id;
    private String initiator;
    private String status;
    private List<ItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
