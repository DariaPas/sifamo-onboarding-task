package com.sifamo.order.infrastructure.adapter.in.web;

import com.sifamo.order.api.DefaultApi;
import com.sifamo.order.api.model.CreateOrderItemRequest;
import com.sifamo.order.api.model.CreateOrderRequest;
import com.sifamo.order.api.model.Order;
import com.sifamo.order.api.model.OrderItem;
import com.sifamo.order.api.model.UpdateStatusRequest;
import com.sifamo.order.application.port.in.CreateOrderCommand;
import com.sifamo.order.application.port.in.CreateOrderItemCommand;
import com.sifamo.order.application.port.in.CreateOrderUseCase;
import com.sifamo.order.application.port.in.GetOrderUseCase;
import com.sifamo.order.application.port.in.ListOrdersUseCase;
import com.sifamo.order.application.port.in.UpdateOrderStatusUseCase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderController implements DefaultApi {
	
	private final CreateOrderUseCase createOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    
    public OrderController(
            CreateOrderUseCase createOrderUseCase,
            ListOrdersUseCase listOrdersUseCase,
            GetOrderUseCase getOrderUseCase,
            UpdateOrderStatusUseCase updateOrderStatusUseCase
    ) {
        this.createOrderUseCase = createOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
    }

    @Override
    public ResponseEntity<List<Order>> ordersGet(UUID customerId) {
    	List<Order> orders = listOrdersUseCase.listOrders(customerId)
                .stream()
                .map(this::toApiOrder)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @Override
    public ResponseEntity<Order> ordersPost(UUID idempotencyKey, CreateOrderRequest request) {
    	CreateOrderCommand command = new CreateOrderCommand(
                request.getCustomerId(),
                request.getShippingAddressId(),
                request.getItems()
                        .stream()
                        .map(this::toCreateOrderItemCommand)
                        .toList()
        );

        com.sifamo.order.domain.model.Order createdOrder =
                createOrderUseCase.createOrder(idempotencyKey, command);
        
    	return ResponseEntity.status(201).body(toApiOrder(createdOrder));
    }

    @Override
    public ResponseEntity<Order> ordersIdGet(UUID id) {
    	return getOrderUseCase.getOrder(id)
                .map(order -> ResponseEntity.ok(toApiOrder(order)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Order> ordersIdStatusPatch(UUID id, UpdateStatusRequest request) {
    	 com.sifamo.order.domain.model.OrderStatus newStatus =
    	            com.sifamo.order.domain.model.OrderStatus.valueOf(request.getStatus().name());

    	    return updateOrderStatusUseCase.updateStatus(id, newStatus)
    	            .map(order -> ResponseEntity.ok(toApiOrder(order)))
    	            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    
    private CreateOrderItemCommand toCreateOrderItemCommand(CreateOrderItemRequest request) {
        return new CreateOrderItemCommand(
                request.getProductId(),
                request.getQuantity()
        );
    }
    
    private Order toApiOrder(com.sifamo.order.domain.model.Order domainOrder) {
        Order apiOrder = new Order();
        apiOrder.setId(domainOrder.getId());
        apiOrder.setCustomerId(domainOrder.getCustomerId());
        apiOrder.setShippingAddressId(domainOrder.getShippingAddressId());
        apiOrder.setStatus(toApiStatus(domainOrder.getStatus()));
        apiOrder.setCreatedAt(domainOrder.getCreatedAt());

        List<OrderItem> apiItems = domainOrder.getItems()
                .stream()
                .map(this::toApiOrderItem)
                .toList();

        apiOrder.setItems(apiItems);

        return apiOrder;
    }

    private OrderItem toApiOrderItem(com.sifamo.order.domain.model.OrderItem domainItem) {
        OrderItem apiItem = new OrderItem();
        apiItem.setId(domainItem.getId());
        apiItem.setProductId(domainItem.getProductId());
        apiItem.setQuantity(domainItem.getQuantity());

        return apiItem;
    }

    private com.sifamo.order.api.model.OrderStatus toApiStatus(
            com.sifamo.order.domain.model.OrderStatus domainStatus
    ) {
        return com.sifamo.order.api.model.OrderStatus.valueOf(domainStatus.name());
    }
}
