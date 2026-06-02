package com.rutusoft.flowable.dto;

import lombok.Data;

import java.util.Map;

@Data
public class MessageEventDto {
    private String processInstanceId;
    private String messageReference;
    private Map<String, Object> eventData;
}
