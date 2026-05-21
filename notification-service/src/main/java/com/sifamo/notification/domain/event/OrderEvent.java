package com.sifamo.notification.domain.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderEvent(
		UUID eventId,
        UUID orderId,
        UUID customerId,
        String eventType,
        String status,
        OffsetDateTime occurredAt
) {
}