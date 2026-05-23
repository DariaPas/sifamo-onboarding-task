
## Services

| Service | Responsibility | Port | README |
|---|---|---:|---|
| Customer Service | Manages customers and addresses | 8081 | [customer-service/README.md](customer-service/README.md) |
| Order Service | Creates orders and publishes Kafka events | 8082 | [order-service/README.md](order-service/README.md) |
| Notification Service | Consumes order events and stores notifications | 8083 | [notification-service/README.md](notification-service/README.md) |

## Infrastructure

| Component | Port |
|---|---:|
| PostgreSQL | 5432 |
| Kafka | 9092 |
| Kafka UI | 8085 |

## Main Flow

```text
Customer Service
        ↑ REST validation
Order Service
        ↓ Kafka order-events
Notification Service
        ↓
notification_schema.notifications
```

## Start Infrastructure

From project root:

```bash
docker compose up -d
```

Check running containers:

```bash
docker compose ps
```

Stop infrastructure:

```bash
docker compose down
```

## Kafka UI

Kafka UI is available at:

```text
http://localhost:8085
```

Use it to inspect the topic:

```text
order-events
```

## Start Services

Start every service in a separate terminal.

Customer Service:

```bash
cd customer-service
./mvnw spring-boot:run
```

Order Service:

```bash
cd order-service
./mvnw spring-boot:run
```

Notification Service:

```bash
cd notification-service
./mvnw spring-boot:run
```

On Windows PowerShell use:

```powershell
.\mvnw spring-boot:run
```

## Database Access From Docker

Open PostgreSQL inside the Docker container:

```bash
docker exec -it sifamo-postgres psql -U postgres -d sifamo_onboarding
```

List schemas:

```sql
\dn
```

List tables in each schema:

```sql
\dt customer_schema.*
\dt order_schema.*
\dt notification_schema.*
```

## Database Schemas

The project uses one PostgreSQL database with three separated schemas:

```text
customer_schema
order_schema
notification_schema
```

Each service has its own database user:

```text
customer_app -> customer_schema
order_app    -> order_schema
notif_app    -> notification_schema
```

Services should access only their own schema.

## Useful Database Queries

Check customers:

```sql
SELECT id, first_name, last_name, email
FROM customer_schema.customers;
```

Check shipping addresses:

```sql
SELECT id, customer_id, street, city, postal_code, country
FROM customer_schema.shipping_addresses;
```

Check orders:

```sql
SELECT id, customer_id, shipping_address_id, status, created_at
FROM order_schema.orders
ORDER BY created_at DESC;
```

Check order items:

```sql
SELECT id, order_id, product_id, quantity
FROM order_schema.order_items
ORDER BY order_id;
```

Check notifications:

```sql
SELECT event_id, order_id, event_type, status, received_at
FROM notification_schema.notifications
ORDER BY received_at DESC;
```

## Cross-Schema Access Tests

These tests prove that services cannot read each other's schemas directly.

### Order Service cannot read Customer Service tables

```sql
SET ROLE order_app;

SELECT * FROM customer_schema.customers;
```

Expected result:

```text
ERROR:  permission denied for schema customer_schema
```

Reset role:

```sql
RESET ROLE;
```

### Customer Service cannot read Order Service tables

```sql
SET ROLE customer_app;

SELECT * FROM order_schema.orders;
```

Expected result:

```text
ERROR:  permission denied for schema order_schema
```

Reset role:

```sql
RESET ROLE;
```

### Customer Service cannot read Notification Service tables

```sql
SET ROLE customer_app;

SELECT * FROM notification_schema.notifications;
```

Expected result:

```text
ERROR:  permission denied for schema notification_schema
```

Reset role:

```sql
RESET ROLE;
```

## Run Tests

Customer Service:

```bash
cd customer-service
./mvnw test
```

Order Service:

```bash
cd order-service
./mvnw test
```

Notification Service:

```bash
cd notification-service
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw test
```

## Architecture Summary

Each service follows Hexagonal Architecture.

General package structure:

```text
com.sifamo.<service>
├── domain
├── application
│   ├── port
│   │   ├── in
│   │   └── out
│   └── usecase
└── infrastructure
    └── adapter
        ├── in
        └── out
```

Dependency direction:

```text
infrastructure -> application -> domain
```

Domain models are independent from Spring, JPA, REST and Kafka.

## API First

Customer Service and Order Service use OpenAPI specifications as API contracts.

Service-specific OpenAPI details are described in the service READMEs.

## Known Limitations and Future Improvements

### Replace `ddl-auto:update` with Flyway

Currently the services use Hibernate `ddl-auto:update` for local development.

A production-grade version should use Flyway migrations instead.

This would avoid manual database changes when enums or table structures change.

Example issue encountered during development:

```text
CONFIRMED was added to the order status enum.
The existing PostgreSQL check constraint still allowed only the old values.
The database constraint had to be changed manually.
```

With Flyway, this would be handled in a migration file.


### Dockerize Spring Services

Currently Docker Compose starts infrastructure services.

A future improvement is to add Dockerfiles for:

```text
customer-service
order-service
notification-service
```

Then all services could be started with Docker Compose.
