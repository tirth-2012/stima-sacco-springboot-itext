package com.rutusoft.flowable.repository;

import com.rutusoft.flowable.entity.AppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {
    Page<AppNotification> findBySentTo(String sentTo, Pageable pageable);
}
