package com.sifamo.order.application.port.in;

import com.sifamo.order.domain.model.Order;

import java.util.UUID;

public interface CreateOrderUseCase {

    Order createOrder(UUID idempotencyKey, CreateOrderCommand command);
}