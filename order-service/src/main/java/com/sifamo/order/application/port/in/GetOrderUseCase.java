package com.sifamo.order.application.port.in;

import com.sifamo.order.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface GetOrderUseCase {

    Optional<Order> getOrder(UUID id);
}