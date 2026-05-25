# Order Service

## Responsibility

Creates and manages orders.

Order Service is responsible for:

- Creating orders.
- Listing orders.
- Getting order details.
- Updating order status.
- Validating customer and shipping address data through Customer Service REST API.
- Publishing order events to Kafka.

Order Service does not read Customer Service database tables directly. Customer validation is done through Customer Service REST API.

Order Service publishes Kafka events when an order is created or when the order status changes.

## Build

```bash
./mvnw clean compile
```

## Run

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw spring-boot:run
```

## Service URL

```text
http://localhost:8082
```

## Endpoints

```text
GET   /
GET   /orders
POST  /orders
GET   /orders/{id}
PATCH /orders/{id}/status
```

## Order Statuses

Supported statuses:

```text
CREATED
CONFIRMED
SHIPPED
CANCELLED
```

## Order Status Lifecycle

Implemented status transitions:

```text
CREATED   -> CONFIRMED
CREATED   -> CANCELLED
CONFIRMED -> SHIPPED
CONFIRMED -> CANCELLED
SHIPPED   -> final status
CANCELLED -> final status
```

Invalid transitions are rejected.

Example invalid transition:

```text
SHIPPED -> CREATED
```

Duplicate status updates do not publish a new Kafka event. For example, if an order is already `SHIPPED`, another request with status `SHIPPED` returns the order but does not create another `OrderShipped` event.

## Validation and Errors

- Invalid request body returns `400 Bad Request`.
- Unknown order returns `404 Not Found`.
- Missing or invalid customer data returns an error response.
- Missing or invalid shipping address data returns an error response.
- Invalid order status transition is rejected.
- The `Idempotency-Key` header is accepted by the API.


## Manual Test Examples

The following examples can be executed with PowerShell after these services are running:

```text
Customer Service: http://localhost:8081
Order Service:    http://localhost:8082
Kafka:            localhost:9092
```

Before creating an order, create a customer and shipping address in Customer Service. Use the returned `customerId` and `shippingAddressId` in the order request.

### Check Service

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8082/" `
  -Method Get
```

Expected response:

```text
Order Service is running
```

### List Orders

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8082/orders" `
  -Method Get
```

Expected result:

```text
200 OK
```

The response is a JSON array of orders.

### Create Order

Replace `<CUSTOMER_ID>` and `<SHIPPING_ADDRESS_ID>` with values returned from Customer Service.

```powershell
$body = @{
  customerId = "<CUSTOMER_ID>"
  shippingAddressId = "<SHIPPING_ADDRESS_ID>"
  items = @(
    @{
      productId = "77777777-7777-7777-7777-777777777777"
      quantity = 1
    }
  )
} | ConvertTo-Json -Depth 4

Invoke-RestMethod `
  -Uri "http://localhost:8082/orders" `
  -Method Post `
  -ContentType "application/json" `
  -Headers @{ "Idempotency-Key" = "111e8400-e29b-41d4-a716-446655440030" } `
  -Body $body
```

Expected result:

```text
201 Created
```

Example response:

```json
{
  "id": "order-uuid",
  "customerId": "customer-uuid",
  "shippingAddressId": "shipping-address-uuid",
  "status": "CREATED",
  "createdAt": "2026-05-22T13:43:22.1488452Z",
  "items": [
    {
      "id": "order-item-uuid",
      "productId": "77777777-7777-7777-7777-777777777777",
      "quantity": 1
    }
  ]
}
```

Expected side effect:

```text
OrderCreated event is published to Kafka topic order-events.
Notification Service consumes the event and stores a notification.
```

### Get Order By ID

Replace `<ORDER_ID>` with the ID returned from the create order request.

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8082/orders/<ORDER_ID>" `
  -Method Get
```

Expected result:

```text
200 OK
```

### Confirm Order

```powershell
$body = @{
  status = "CONFIRMED"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8082/orders/<ORDER_ID>/status" `
  -Method Patch `
  -ContentType "application/json" `
  -Body $body
```

Expected result:

```text
200 OK
```

Expected side effect:

```text
OrderConfirmed event is published to Kafka topic order-events.
Notification Service consumes the event and stores a notification.
```

### Ship Order

This request should be executed after the order was confirmed.

```powershell
$body = @{
  status = "SHIPPED"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8082/orders/<ORDER_ID>/status" `
  -Method Patch `
  -ContentType "application/json" `
  -Body $body
```

Expected result:

```text
200 OK
```

Expected side effect:

```text
OrderShipped event is published to Kafka topic order-events.
Notification Service consumes the event and stores a notification.
```

### Cancel Order

A created or confirmed order can be cancelled.

```powershell
$body = @{
  status = "CANCELLED"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8082/orders/<ORDER_ID>/status" `
  -Method Patch `
  -ContentType "application/json" `
  -Body $body
```

Expected result:

```text
200 OK
```

Expected side effect:

```text
OrderCancelled event is published to Kafka topic order-events.
Notification Service consumes the event and stores a notification.
```

### Invalid Create Order Request

This example sends an empty items list.

```powershell
$body = @{
  customerId = "<CUSTOMER_ID>"
  shippingAddressId = "<SHIPPING_ADDRESS_ID>"
  items = @()
} | ConvertTo-Json -Depth 4

Invoke-RestMethod `
  -Uri "http://localhost:8082/orders" `
  -Method Post `
  -ContentType "application/json" `
  -Headers @{ "Idempotency-Key" = "222e8400-e29b-41d4-a716-446655440040" } `
  -Body $body
```

Expected result:

```text
400 Bad Request
```

The response is a `ProblemDetail` JSON object with validation errors.

### Invalid Status Transition

This example tries to change a shipped order back to created.

```powershell
$body = @{
  status = "CREATED"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8082/orders/<SHIPPED_ORDER_ID>/status" `
  -Method Patch `
  -ContentType "application/json" `
  -Body $body
```

Expected result:

```text
Invalid order status transition: SHIPPED -> CREATED
```

Currently this is handled through a generic exception handler. A future improvement is to map invalid transitions to `400 Bad Request` with a dedicated domain exception.

### Unknown Order

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8082/orders/00000000-0000-0000-0000-000000000000" `
  -Method Get
```

Expected result:

```text
404 Not Found
```

## Events Published

Order Service publishes events to Kafka topic:

```text
order-events
```

Events are published when:

```text
POST /orders
PATCH /orders/{id}/status
```

Published event types:

```text
OrderCreated
OrderConfirmed
OrderShipped
OrderCancelled
```

Event format:

```json
{
  "eventId": "uuid",
  "orderId": "uuid",
  "customerId": "uuid",
  "eventType": "OrderCreated",
  "status": "CREATED",
  "occurredAt": "2026-05-22T09:17:03Z"
}
```

Event fields:

| Field | Meaning |
|---|---|
| `eventId` | Unique ID of the event. Used by Notification Service for idempotency. |
| `orderId` | ID of the order. |
| `customerId` | ID of the customer. |
| `eventType` | Business event name. |
| `status` | Current order status. |
| `occurredAt` | Time when the event was created. |

Kafka message key:

```text
orderId
```

## Automated Test Description

The service contains REST controller tests for the Order API.

Run tests:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw test
```


### Test: GET /orders

This test calls:

```text
GET /orders
```

The mocked `ListOrdersUseCase` returns an empty list.

Expected result:

```text
200 OK
[]
```

This proves that the list orders endpoint is reachable and returns a JSON response.

### Test: Valid POST /orders

This test sends a valid order request with:

```text
customerId
shippingAddressId
items
Idempotency-Key header
```

Expected result:

```text
201 Created
```

The  `CreateOrderUseCase` returns a created order with status `CREATED`.

### Test: Invalid POST /orders

This test sends an invalid order request with an empty `items` list.

Expected result:

```text
400 Bad Request
```

This proves that request validation works and an order cannot be created without order items.

### Test: Valid PATCH /orders/{id}/status

This test sends a valid status update request:

```json
{
  "status": "CONFIRMED"
}
```

Expected result:

```text
200 OK
```

The `UpdateOrderStatusUseCase` returns an updated order with status `CONFIRMED`.

This proves that the controller accepts a valid status update and returns the updated order.

### Test: Invalid PATCH /orders/{id}/status

This test simulates an invalid status transition:

```text
CONFIRMED -> CREATED
```

The mocked `UpdateOrderStatusUseCase` throws an exception.

Expected result:

```text
error response
```

This proves that invalid status transitions are rejected and returned as an error response.

## Database

Order Service stores data in PostgreSQL schema:

```text
order_schema
```

Main tables:

```text
orders
order_items
```

Order Service should only access `order_schema`.

Order Service should not read `customer_schema` directly. Customer validation is done through Customer Service REST API.


## OpenAPI

The Order API contract is defined in:

```text
src/main/resources/openapi/order-api.yaml
```

Order Service also uses a copy of the Customer API contract for the generated Customer Service REST client:

```text
src/main/resources/openapi/customer-api.yaml
```

The Customer Service client adapter uses generated client code to call Customer Service.
