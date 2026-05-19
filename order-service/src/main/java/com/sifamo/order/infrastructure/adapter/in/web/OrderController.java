package com.sifamo.order.infrastructure.adapter.in.web;

import com.sifamo.order.api.DefaultApi;
import com.sifamo.order.api.model.CreateOrderRequest;
import com.sifamo.order.api.model.Order;
import com.sifamo.order.api.model.UpdateStatusRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderController implements DefaultApi {

    @Override
    public ResponseEntity<List<Order>> ordersGet(UUID customerId) {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Order> ordersPost(UUID idempotencyKey, CreateOrderRequest createOrderRequest) {
        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<Order> ordersIdGet(UUID id) {
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Order> ordersIdStatusPatch(UUID id, UpdateStatusRequest updateStatusRequest) {
        return ResponseEntity.ok().build();
    }
}