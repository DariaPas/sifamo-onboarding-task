package com.sifamo.notification.application.usecase;

import com.sifamo.notification.application.port.in.HandleOrderEventUseCase;
import com.sifamo.notification.application.port.out.NotificationRepositoryPort;
import com.sifamo.notification.domain.event.OrderEvent;
import com.sifamo.notification.domain.model.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class NotificationUseCaseService implements HandleOrderEventUseCase {

    private final NotificationRepositoryPort notificationRepositoryPort;

    public NotificationUseCaseService(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Override
    @Transactional
    public void handle(OrderEvent event) {
        Notification notification = new Notification(
                UUID.randomUUID(),
                event.orderId(),
                event.customerId(),
                event.eventType(),
                event.status(),
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        notificationRepositoryPort.save(notification);
    }
}