package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.MessageEventDto;
import org.springframework.web.bind.annotation.RequestBody;

public interface MessageEventService {
    String sendMessage(MessageEventDto messageEventDto);
}
