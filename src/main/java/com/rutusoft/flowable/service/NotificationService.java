package com.rutusoft.flowable.service;

import com.rutusoft.flowable.dto.NotificationRequest;
import com.rutusoft.flowable.dto.NotificationResponse;
import org.springframework.data.domain.Page;

public interface NotificationService {
    public Page<NotificationResponse> getNotificationsBySentTo(String sentTo, int page, int size);
    public NotificationResponse createNotification(NotificationRequest request);
    public void markAsRead(Long id);
}
