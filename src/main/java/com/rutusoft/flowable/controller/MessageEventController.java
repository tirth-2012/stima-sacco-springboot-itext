package com.rutusoft.flowable.controller;

import com.rutusoft.flowable.dto.MessageEventDto;
import com.rutusoft.flowable.service.MessageEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageEventController {

    @Autowired
    private MessageEventService messageEventService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageEventDto messageEventDto) {
        return new ResponseEntity<>(messageEventService.sendMessage(messageEventDto), HttpStatus.CREATED);
    }
}
