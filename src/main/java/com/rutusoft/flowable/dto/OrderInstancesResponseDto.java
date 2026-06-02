package com.rutusoft.flowable.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderInstancesResponseDto {

    private List<OrderResponseDto> orders;
    private int from;
    private int to;
    private long total;
}