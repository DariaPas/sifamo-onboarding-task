package com.sifamo.notification.infrastructure.adapter.out.persistence;

import com.sifamo.notification.application.port.out.NotificationRepositoryPort;
import com.sifamo.notification.domain.model.Notification;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceAdapter implements NotificationRepositoryPort {

    private final SpringDataNotificationRepository springDataNotificationRepository;

    public NotificationPersistenceAdapter(SpringDataNotificationRepository springDataNotificationRepository) {
        this.springDataNotificationRepository = springDataNotificationRepository;
    }

    @Override
    public Notification save(Notification notification) {
        NotificationJpaEntity entity = new NotificationJpaEntity(
                notification.getId(),
                notification.getEventId(),
                notification.getOrderId(),
                notification.getCustomerId(),
                notification.getEventType(),
                notification.getStatus(),
                notification.getReceivedAt()
        );

        NotificationJpaEntity savedEntity = springDataNotificationRepository.save(entity);

        return new Notification(
                savedEntity.getId(),
                savedEntity.getEventId(),
                savedEntity.getOrderId(),
                savedEntity.getCustomerId(),
                savedEntity.getEventType(),
                savedEntity.getStatus(),
                savedEntity.getReceivedAt()
        );
    }
    
    @Override
    public boolean existsByEventId(UUID eventId) {
        return springDataNotificationRepository.existsByEventId(eventId);
    }
}