package com.sifamo.order.application.port.out;

import com.sifamo.order.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {

    Order save(Order order);

    List<Order> findAll();

    List<Order> findByCustomerId(UUID customerId);

    Optional<Order> findById(UUID id);
}
