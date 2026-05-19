package com.sifamo.order.domain.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
	private final UUID id;
    private final UUID customerId;
    private final UUID shippingAddressId;
    private final OrderStatus status;
    private final OffsetDateTime createdAt;
    private final List<OrderItem> items;

    public Order(
            UUID id,
            UUID customerId,
            UUID shippingAddressId,
            OrderStatus status,
            OffsetDateTime createdAt,
            List<OrderItem> items
    ) {
        this.id = id;
        this.customerId = customerId;
        this.shippingAddressId = shippingAddressId;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public UUID getShippingAddressId() {
        return shippingAddressId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

}
