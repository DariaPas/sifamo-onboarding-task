package com.sifamo.order.application.usecase;

import com.sifamo.order.application.port.in.CreateOrderCommand;
import com.sifamo.order.application.port.in.CreateOrderItemCommand;
import com.sifamo.order.application.port.in.CreateOrderUseCase;
import com.sifamo.order.application.port.in.GetOrderUseCase;
import com.sifamo.order.application.port.in.ListOrdersUseCase;
import com.sifamo.order.application.port.in.UpdateOrderStatusUseCase;
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
import com.sifamo.order.application.port.out.OrderEventPublisherPort;
import com.sifamo.order.domain.event.OrderEvent;

@Service
public class OrderUseCaseService implements CreateOrderUseCase, ListOrdersUseCase, GetOrderUseCase, UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final CustomerValidationPort customerValidationPort;
    private final OrderEventPublisherPort orderEventPublisherPort;
    
    public OrderUseCaseService(OrderRepositoryPort orderRepositoryPort, CustomerValidationPort customerValidationPort,
    		OrderEventPublisherPort orderEventPublisherPort) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.customerValidationPort = customerValidationPort;
        this.orderEventPublisherPort = orderEventPublisherPort;
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
        
        Order savedOrder = orderRepositoryPort.save(order);

        orderEventPublisherPort.publish(new OrderEvent(
        		UUID.randomUUID(),
                savedOrder.getId(),
                savedOrder.getCustomerId(),
                "OrderCreated",
                savedOrder.getStatus().name(),
                OffsetDateTime.now(ZoneOffset.UTC)
        ));

        return savedOrder;
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
    
    @Override
    @Transactional
    public Optional<Order> updateStatus(UUID orderId, OrderStatus newStatus) {
    	return orderRepositoryPort.findById(orderId)
                .map(order -> {
                	 if (order.getStatus() == newStatus) {
                         return order;
                     }
                	 if (!isValidTransition(order.getStatus(), newStatus)) {
                         throw new IllegalArgumentException(
                                 "Invalid order status transition: " + order.getStatus() + " -> " + newStatus
                         );
                     }

                    Order updatedOrder = orderRepositoryPort.save(order.withStatus(newStatus));

                    String eventType = switch (newStatus) {
                    	case CONFIRMED -> "OrderConfirmed";
                        case SHIPPED -> "OrderShipped";
                        case CANCELLED -> "OrderCancelled";
                        default -> "OrderStatusUpdated";
                    };

                    orderEventPublisherPort.publish(new OrderEvent(
                    		UUID.randomUUID(),
                            updatedOrder.getId(),
                            updatedOrder.getCustomerId(),
                            eventType,
                            updatedOrder.getStatus().name(),
                            OffsetDateTime.now(ZoneOffset.UTC)
                    ));

                    return updatedOrder;
                });    }
    
    private boolean isValidTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        return switch (currentStatus) {
            case CREATED -> newStatus == OrderStatus.CONFIRMED
                    || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.SHIPPED
                    || newStatus == OrderStatus.CANCELLED;
            case SHIPPED, CANCELLED -> false;
        };
    }
}