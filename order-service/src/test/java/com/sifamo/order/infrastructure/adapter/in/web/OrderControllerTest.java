package com.sifamo.order.infrastructure.adapter.in.web;

import com.sifamo.order.application.port.in.CreateOrderCommand;
import com.sifamo.order.application.port.in.CreateOrderUseCase;
import com.sifamo.order.application.port.in.GetOrderUseCase;
import com.sifamo.order.application.port.in.ListOrdersUseCase;
import com.sifamo.order.application.port.in.UpdateOrderStatusUseCase;
import com.sifamo.order.domain.model.Order;
import com.sifamo.order.domain.model.OrderItem;
import com.sifamo.order.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateOrderUseCase createOrderUseCase;

    @MockitoBean
    private ListOrdersUseCase listOrdersUseCase;

    @MockitoBean
    private GetOrderUseCase getOrderUseCase;

    @MockitoBean
    private UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @Test
    void ordersGetReturnsOk() throws Exception {
        when(listOrdersUseCase.listOrders(null)).thenReturn(List.of());

        mockMvc.perform(get("/orders"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void ordersPostWithValidBodyReturnsCreatedOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.fromString("4685bd19-6989-4e39-ac1c-f430e8cdbf15");
        UUID shippingAddressId = UUID.fromString("d2815998-01d3-4325-b41b-dcfa0edf6d53");
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.fromString("77777777-7777-7777-7777-777777777777");

        Order order = new Order(
                orderId,
                customerId,
                shippingAddressId,
                OrderStatus.CREATED,
                OffsetDateTime.now(ZoneOffset.UTC),
                List.of(new OrderItem(itemId, productId, 1))
        );

        when(createOrderUseCase.createOrder(any(UUID.class), any(CreateOrderCommand.class)))
                .thenReturn(order);

        String body = """
                {
                  "customerId": "4685bd19-6989-4e39-ac1c-f430e8cdbf15",
                  "shippingAddressId": "d2815998-01d3-4325-b41b-dcfa0edf6d53",
                  "items": [
                    {
                      "productId": "77777777-7777-7777-7777-777777777777",
                      "quantity": 1
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/orders")
                        .header("Idempotency-Key", "111e8400-e29b-41d4-a716-446655440030")
                        .contentType("application/json")
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.shippingAddressId").value(shippingAddressId.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.items[0].productId").value(productId.toString()))
                .andExpect(jsonPath("$.items[0].quantity").value(1));
    }

    @Test
    void ordersPostWithEmptyItemsReturnsBadRequest() throws Exception {
        String body = """
                {
                  "customerId": "4685bd19-6989-4e39-ac1c-f430e8cdbf15",
                  "shippingAddressId": "d2815998-01d3-4325-b41b-dcfa0edf6d53",
                  "items": []
                }
                """;

        mockMvc.perform(post("/orders")
                        .header("Idempotency-Key", "222e8400-e29b-41d4-a716-446655440040")
                        .contentType("application/json")
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void ordersStatusPatchWithValidStatusReturnsUpdatedOrder() throws Exception {
        UUID orderId = UUID.fromString("6003245d-23b6-4065-af21-2aeb71357a43");
        UUID customerId = UUID.fromString("4685bd19-6989-4e39-ac1c-f430e8cdbf15");
        UUID shippingAddressId = UUID.fromString("d2815998-01d3-4325-b41b-dcfa0edf6d53");

        Order updatedOrder = new Order(
                orderId,
                customerId,
                shippingAddressId,
                OrderStatus.CONFIRMED,
                OffsetDateTime.now(ZoneOffset.UTC),
                List.of()
        );

        when(updateOrderStatusUseCase.updateStatus(eq(orderId), eq(OrderStatus.CONFIRMED)))
                .thenReturn(Optional.of(updatedOrder));

        String body = """
                {
                  "status": "CONFIRMED"
                }
                """;

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .contentType("application/json")
                        .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void ordersStatusPatchWithInvalidTransitionReturnsError() throws Exception {
        UUID orderId = UUID.fromString("6003245d-23b6-4065-af21-2aeb71357a43");

        when(updateOrderStatusUseCase.updateStatus(eq(orderId), eq(OrderStatus.CREATED)))
                .thenThrow(new IllegalArgumentException("Invalid order status transition: CONFIRMED -> CREATED"));

        String body = """
                {
                  "status": "CREATED"
                }
                """;

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .contentType("application/json")
                        .content(body))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}