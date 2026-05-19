package com.sifamo.order.application.usecase;

import com.sifamo.order.application.port.in.CreateOrderCommand;
import com.sifamo.order.application.port.in.CreateOrderItemCommand;
import com.sifamo.order.application.port.in.CreateOrderUseCase;
import com.sifamo.order.application.port.in.GetOrderUseCase;
import com.sifamo.order.application.port.in.ListOrdersUseCase;
import com.sifamo.order.application.port.out.OrderRepositoryPort;
import com.sifamo.order.domain.model.Order;
import com.sifamo.order.domain.model.OrderItem;
import com.sifamo.order.domain.model.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import com.sifamo.order.application.port.out.CustomerValidationPort;

@Service
public class OrderUseCaseService implements CreateOrderUseCase, ListOrdersUseCase, GetOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final CustomerValidationPort customerValidationPort;

    public OrderUseCaseService(OrderRepositoryPort orderRepositoryPort, CustomerValidationPort customerValidationPort) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.customerValidationPort = customerValidationPort;
    }

    @Override
    @Transactional
    public Order createOrder(UUID idempotencyKey, CreateOrderCommand command) {
    	
    	if (!customerValidationPort.customerExists(command.customerId())) {
    	    throw new IllegalArgumentException("Customer not found: " + command.customerId());
    	}

    	if (!customerValidationPort.shippingAddressExists(command.customerId(), command.shippingAddressId())) {
    	    throw new IllegalArgumentException("Shipping address not found: " + command.shippingAddressId());
    	}
    	
        List<OrderItem> items = command.items()
                .stream()
                .map(this::toDomainItem)
                .toList();

        Order order = new Order(
                UUID.randomUUID(),
                command.customerId(),
                command.shippingAddressId(),
                OrderStatus.CREATED,
                OffsetDateTime.now(ZoneOffset.UTC),
                items
        );

        return orderRepositoryPort.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> listOrders(UUID customerId) {
        if (customerId != null) {
            return orderRepositoryPort.findByCustomerId(customerId);
        }

        return orderRepositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrder(UUID id) {
        return orderRepositoryPort.findById(id);
    }

    private OrderItem toDomainItem(CreateOrderItemCommand command) {
        return new OrderItem(
                UUID.randomUUID(),
                command.productId(),
                command.quantity()
        );
    }
}