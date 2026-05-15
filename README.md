## Current Status

- `customer-service` and `order-service` project skeletons created
- Initial hexagonal/layered package structure added
- OpenAPI specifications created and validated with Swagger Editor
- Basic test endpoints added for local startup verification

## OpenAPI Specifications

Customer Service:
```text
customer-service/src/main/resources/openapi/customer-api.yaml
```
Order Service:
```text
order-service/src/main/resources/openapi/order-api.yaml
```
## Notes
Customer Service contains an additional endpoint for validating whether an address belongs to a specific customer:
```text
GET /customers/{id}/addresses/{addressId}
```
Notification Service is currently planned as a Kafka consumer without a dedicated REST API.
