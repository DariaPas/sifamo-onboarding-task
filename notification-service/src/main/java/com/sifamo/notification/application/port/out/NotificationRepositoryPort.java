package com.sifamo.notification.application.port.out;

import java.util.UUID;

import com.sifamo.notification.domain.model.Notification;

public interface NotificationRepositoryPort {

    Notification save(Notification notification);
    boolean existsByEventId(UUID eventId);
}