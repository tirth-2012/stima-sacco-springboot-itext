package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.MessageEventDto;
import com.rutusoft.flowable.service.MessageEventService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MessageEventServiceImpl implements MessageEventService {
    @Autowired
    private RuntimeService runtimeService;

    @Override
    public String sendMessage(MessageEventDto messageEventDto) {
        log.info("triggering message with request : {}", messageEventDto);
        List<Execution> messageExecutions = runtimeService.createExecutionQuery()
                .messageEventSubscriptionName(messageEventDto.getMessageReference())
                .processInstanceId(messageEventDto.getProcessInstanceId())
                .list();

        if(!messageExecutions.isEmpty()) {
            String executionId = messageExecutions.get(0).getId();
            runtimeService.messageEventReceived(messageEventDto.getMessageReference(), executionId, messageEventDto.getEventData());
            return "Message event sent successfully";
        }
        else {
            return "There is no active message catch event exists";
        }
    }
}
