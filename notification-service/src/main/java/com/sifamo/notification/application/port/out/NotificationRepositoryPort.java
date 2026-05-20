package com.sifamo.notification.application.port.out;

import com.sifamo.notification.domain.model.Notification;

public interface NotificationRepositoryPort {

    Notification save(Notification notification);
}