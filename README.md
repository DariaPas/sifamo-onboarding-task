## Current status

Implemented so far:

- Customer OpenAPI spec adjusted
- Order OpenAPI spec adjusted
- OpenAPI Generator configured for Customer Service
- OpenAPI Generator configured for Order Service
- Docker Compose setup added for local infrastructure
- Postgres starts with three schemas/users
- Kafka starts in KRaft mode
- Kafka UI starts and shows the local Kafka cluster online
- Customer Service connects to `customer_schema`
- Customer Service has a first implementation
- Customer endpoints for Customer, BillingAddress and ShippingAddress are working

---

## OpenAPI changes

### Customer Service

The address model is now split into two different concepts:

#### BillingAddress

- Exactly one billing address per customer
- Created together with the customer
- Required in `CreateCustomerRequest`
- Modeled as a 1:1 relationship

#### ShippingAddress

- One customer can have multiple shipping addresses
- Managed through separate endpoints
- Modeled as a 1:n relationship

### OrderService

- Order and CreateOrderRequest now contains an items list
- Contains a copy of:  order-service/src/main/resources/openapi/customer-api.yaml

Current Customer working endpoints:

```text
POST /customers
GET /customers
GET /customers/{id}
POST /customers/{id}/shipping-addresses
GET /customers/{id}/shipping-addresses
GET /customers/{id}/shipping-addresses/{shippingAddressId}
