package com.rutusoft.flowable.service.impl;

import com.rutusoft.flowable.dto.NotificationRequest;
import com.rutusoft.flowable.dto.NotificationResponse;
import com.rutusoft.flowable.entity.AppNotification;
import com.rutusoft.flowable.repository.AppNotificationRepository;
import com.rutusoft.flowable.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final AppNotificationRepository repository;

    private static final int MAX_PAGE_SIZE = 50;

    @Override
    public Page<NotificationResponse> getNotificationsBySentTo(String sentTo, int page, int size) {

        if (sentTo == null || sentTo.trim().isEmpty()) {
            throw new IllegalArgumentException("sentTo must not be empty");
        }

        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("dateTime").descending());

        return repository.findBySentTo(sentTo.trim(), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {

        log.info("Creating notification for user: {}", request.getSentTo());

        AppNotification entity = new AppNotification();
        entity.setProcessInstanceId(request.getProcessInstanceId().trim());
        entity.setReferenceNo(request.getReferenceNo().trim());
        entity.setNotification(request.getNotification().trim());
        entity.setSentTo(request.getSentTo().trim());
        entity.setSentFrom(request.getSentFrom().trim());

        AppNotification saved = repository.save(entity);

        return mapToResponse(saved);
    }

    private NotificationResponse mapToResponse(AppNotification entity) {

        if (entity.getDateTime() == null) {
            log.warn("dateTime is null for notification id={}", entity.getId());
        }

        NotificationResponse response = new NotificationResponse();
        response.setId(entity.getId());
        response.setProcessInstanceId(entity.getProcessInstanceId());
        response.setReferenceNo(entity.getReferenceNo());
        response.setNotification(entity.getNotification());
        response.setSentTo(entity.getSentTo());
        response.setSentFrom(entity.getSentFrom());
        response.setRead(entity.isRead());
        response.setDateTime(entity.getDateTime());
        return response;
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        AppNotification notification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.isRead()) {
            notification.setRead(true);
        }
    }
}