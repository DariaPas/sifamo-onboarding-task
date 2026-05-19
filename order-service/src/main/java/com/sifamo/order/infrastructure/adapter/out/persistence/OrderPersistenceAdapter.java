package com.sifamo.order.infrastructure.adapter.out.persistence;


import com.sifamo.order.application.port.out.OrderRepositoryPort;
import com.sifamo.order.domain.model.Order;
import com.sifamo.order.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository springDataOrderRepository;

    public OrderPersistenceAdapter(SpringDataOrderRepository springDataOrderRepository) {
        this.springDataOrderRepository = springDataOrderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = toJpaEntity(order);
        OrderJpaEntity savedEntity = springDataOrderRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<Order> findAll() {
        return springDataOrderRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        return springDataOrderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return springDataOrderRepository.findById(id)
                .map(this::toDomain);
    }

    private OrderJpaEntity toJpaEntity(Order order) {
        OrderJpaEntity orderEntity = new OrderJpaEntity(
                order.getId(),
                order.getCustomerId(),
                order.getShippingAddressId(),
                order.getStatus(),
                order.getCreatedAt()
        );

        order.getItems().forEach(item -> {
            OrderItemJpaEntity itemEntity = new OrderItemJpaEntity(
                    item.getId(),
                    item.getProductId(),
                    item.getQuantity()
            );

            orderEntity.addItem(itemEntity);
        });

        return orderEntity;
    }

    private Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> items = entity.getItems()
                .stream()
                .map(this::toDomainItem)
                .toList();

        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getShippingAddressId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                items
        );
    }

    private OrderItem toDomainItem(OrderItemJpaEntity entity) {
        return new OrderItem(
                entity.getId(),
                entity.getProductId(),
                entity.getQuantity()
        );
    }
}