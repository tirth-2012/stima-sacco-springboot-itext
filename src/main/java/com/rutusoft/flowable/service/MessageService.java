package com.rutusoft.flowable.service;

import java.util.Map;

public interface MessageService {
    void triggerMessage(String processInstanceId, String messageName, Map<String, Object> messageData);
}
