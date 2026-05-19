package com.sifamo.order.application.port.in;

import com.sifamo.order.domain.model.Order;
import com.sifamo.order.domain.model.OrderStatus;

import java.util.Optional;
import java.util.UUID;

public interface UpdateOrderStatusUseCase {

    Optional<Order> updateStatus(UUID orderId, OrderStatus newStatus);
}