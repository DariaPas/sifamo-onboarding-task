package com.sifamo.order.domain.model;

import java.util.UUID;

public class OrderItem {
	private final UUID id;
    private final UUID productId;
    private final int quantity;

    public OrderItem(UUID id, UUID productId, int quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

}
