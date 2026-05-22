package com.sifamo.notification.application.usecase;

import com.sifamo.notification.application.port.out.NotificationRepositoryPort;
import com.sifamo.notification.domain.event.OrderEvent;
import com.sifamo.notification.domain.model.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationUseCaseServiceTest {

    @Mock
    private NotificationRepositoryPort notificationRepositoryPort;

    @InjectMocks
    private NotificationUseCaseService notificationUseCaseService;

    @Test
    void handleNewOrderEventSavesNotification() {
        UUID eventId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        OrderEvent event = new OrderEvent(
                eventId,
                orderId,
                customerId,
                "OrderCreated",
                "CREATED",
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        when(notificationRepositoryPort.existsByEventId(eventId)).thenReturn(false);

        notificationUseCaseService.handle(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepositoryPort).save(captor.capture());

        Notification savedNotification = captor.getValue();

        assertThat(savedNotification.getEventId()).isEqualTo(eventId);
        assertThat(savedNotification.getOrderId()).isEqualTo(orderId);
        assertThat(savedNotification.getCustomerId()).isEqualTo(customerId);
        assertThat(savedNotification.getEventType()).isEqualTo("OrderCreated");
        assertThat(savedNotification.getStatus()).isEqualTo("CREATED");
        assertThat(savedNotification.getReceivedAt()).isNotNull();
    }

    @Test
    void handleDuplicateOrderEventDoesNotSaveNotification() {
        UUID eventId = UUID.randomUUID();

        OrderEvent event = new OrderEvent(
                eventId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "OrderShipped",
                "SHIPPED",
                OffsetDateTime.now(ZoneOffset.UTC)
        );

        when(notificationRepositoryPort.existsByEventId(eventId)).thenReturn(true);

        notificationUseCaseService.handle(event);

        verify(notificationRepositoryPort, never()).save(org.mockito.ArgumentMatchers.any(Notification.class));
    }
}