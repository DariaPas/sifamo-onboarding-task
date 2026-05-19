package com.sifamo.order.application.port.in;

import java.util.UUID;

public record CreateOrderItemCommand(
        UUID productId,
        int quantity
) {
}