package com.sifamo.order.application.port.in;

import com.sifamo.order.domain.model.Order;

import java.util.List;
import java.util.UUID;

public interface ListOrdersUseCase {

    List<Order> listOrders(UUID customerId);
}