# Notification Service

## Responsibility

Consumes order events from Kafka and stores notifications.

Notification Service is responsible for:

- Listening to Kafka topic `order-events`.
- Consuming order events published by Order Service.
- Saving received events as notifications.
- Avoiding duplicate notification records by using `eventId`.

Notification Service does not create orders and does not manage customers. It reacts to events from Order Service.

This service has no business REST API. It only has a small service check endpoint.

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
http://localhost:8083
```

## Endpoint

```text
GET /
```

`GET /` returns:

```text
Notification Service is running
```

## Events Consumed

Notification Service consumes events from Kafka topic:

```text
order-events
```

Consumed event types:

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
| `eventId` | Unique ID of the event. Used for idempotency. |
| `orderId` | ID of the order. |
| `customerId` | ID of the customer. |
| `eventType` | Business event name. |
| `status` | Current order status. |
| `occurredAt` | Time when the event was created by Order Service. |

## Idempotency

Kafka can deliver the same message more than once. Notification Service prevents duplicate notification records by checking the `eventId`.

Processing logic:

```text
new eventId      -> save notification
existing eventId -> skip duplicate
```

The `eventId` column has a unique constraint in the database.

## Manual Test Examples

### Check Service

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8083/" `
  -Method Get
```

Expected response:

```text
Notification Service is running
```

### Trigger Notification By Creating Order

Create an order in Order Service:

```text
POST http://localhost:8082/orders
```

Expected result:

```text
OrderCreated event is published to Kafka topic order-events.
Notification Service consumes the event.
A row is saved in notification_schema.notifications.
```

### Trigger Notification By Confirming Order

Update an order status to `CONFIRMED` in Order Service:

```text
PATCH http://localhost:8082/orders/{id}/status
```

Request body:

```json
{
  "status": "CONFIRMED"
}
```

Expected result:

```text
OrderConfirmed event is published to Kafka topic order-events.
Notification Service consumes the event.
A row is saved in notification_schema.notifications.
```

### Trigger Notification By Shipping Order

Update an order status to `SHIPPED` in Order Service:

```text
PATCH http://localhost:8082/orders/{id}/status
```

Request body:

```json
{
  "status": "SHIPPED"
}
```

Expected result:

```text
OrderShipped event is published to Kafka topic order-events.
Notification Service consumes the event.
A row is saved in notification_schema.notifications.
```

### Trigger Notification By Cancelling Order

Update an order status to `CANCELLED` in Order Service:

```text
PATCH http://localhost:8082/orders/{id}/status
```

Request body:

```json
{
  "status": "CANCELLED"
}
```

Expected result:

```text
OrderCancelled event is published to Kafka topic order-events.
Notification Service consumes the event.
A row is saved in notification_schema.notifications.
```


## Automated Test Description

The service contains unit tests for notification event handling.

Run tests:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw test
```


Notification Service does not have business REST controller tests because it has no business REST API. The main behavior is Kafka event consumption and idempotent notification saving.

### Test: New Event Saves Notification

This test creates a new `OrderEvent` with a new `eventId`.

The  `NotificationRepositoryPort` returns:

```text
existsByEventId(eventId) -> false
```

Expected behavior:

```text
save notification
```

This proves that a new Kafka event is stored as a notification.

### Test: Duplicate Event Does Not Save Notification

This test creates an `OrderEvent` with an already processed `eventId`.

The `NotificationRepositoryPort` returns:

```text
existsByEventId(eventId) -> true
```

Expected behavior:

```text
do not save notification
```
