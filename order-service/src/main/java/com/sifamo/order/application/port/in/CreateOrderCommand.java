package com.sifamo.order.application.port.in;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID customerId,
        UUID shippingAddressId,
        List<CreateOrderItemCommand> items
		) {
}