package com.sifamo.notification.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications", schema = "notification_schema")
public class NotificationJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private OffsetDateTime receivedAt;

    protected NotificationJpaEntity() {
    }

    public NotificationJpaEntity(
            UUID id,
            UUID orderId,
            UUID customerId,
            String eventType,
            String status,
            OffsetDateTime receivedAt
    ) {
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