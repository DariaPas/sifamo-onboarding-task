package com.sifamo.notification.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Notification {

    private final UUID id;
    private final UUID orderId;
    private final UUID customerId;
    private final String eventType;
    private final String status;
    private final OffsetDateTime receivedAt;

    public Notification(UUID id, UUID orderId, UUID customerId, String eventType, String status, OffsetDateTime receivedAt) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.eventType = eventType;
        this.status = status;
        this.receivedAt = receivedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getReceivedAt() {
        return receivedAt;
    }
}