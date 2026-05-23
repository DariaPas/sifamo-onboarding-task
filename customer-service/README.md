# Customer Service

## Responsibility

Manages customers, billing addresses and shipping addresses.

Order Service calls Customer Service via REST to validate customer and shipping address data before creating an order.

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
http://localhost:8081
```

## Endpoints

```text
GET  /
GET  /customers
POST /customers
GET  /customers/{id}
POST /customers/{id}/shipping-addresses
GET  /customers/{id}/shipping-addresses
GET  /customers/{id}/shipping-addresses/{shippingAddressId}
```

## Validation and Errors

- Invalid request body returns `400 Bad Request`.
- Unknown customer returns `404 Not Found`.
- Duplicate email returns `409 Conflict`.

## Manual Test Examples

The following examples can be executed with PowerShell after the service is running on `http://localhost:8081`.

### Check Service

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8081/" `
  -Method Get
```

Expected response:

```text
Customer Service is running
```

### Create Customer

```powershell
$body = @{
  firstName = "Anna"
  lastName = "Meyer"
  email = "anna.meyer@example.com"
  billingAddress = @{
    street = "Example Street 10"
    city = "Hamburg"
    postalCode = "20095"
    country = "DE"
  }
} | ConvertTo-Json -Depth 3

Invoke-RestMethod `
  -Uri "http://localhost:8081/customers" `
  -Method Post `
  -ContentType "application/json" `
  -Body $body
```

Expected result:

```text
201 Created
```

Example response:

```json
{
  "id": "customer-uuid",
  "firstName": "Anna",
  "lastName": "Meyer",
  "email": "anna.meyer@example.com",
  "billingAddress": {
    "id": "billing-address-uuid",
    "street": "Example Street 10",
    "city": "Hamburg",
    "postalCode": "20095",
    "country": "DE"
  }
}
```

Save the returned customer `id`. It is needed for the next examples.

### List Customers

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8081/customers" `
  -Method Get
```

Expected result:

```text
200 OK
```

The response is a JSON array of customers.

### Get Customer By ID

Replace `<CUSTOMER_ID>` with the ID returned from the create customer request.

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8081/customers/<CUSTOMER_ID>" `
  -Method Get
```

Expected result:

```text
200 OK
```

### Add Shipping Address

```powershell
$body = @{
  street = "Shipping Street 20"
  city = "Berlin"
  postalCode = "10115"
  country = "DE"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8081/customers/<CUSTOMER_ID>/shipping-addresses" `
  -Method Post `
  -ContentType "application/json" `
  -Body $body
```

Expected result:

```text
201 Created
```

Example response:

```json
{
  "id": "shipping-address-uuid",
  "street": "Shipping Street 20",
  "city": "Berlin",
  "postalCode": "10115",
  "country": "DE"
}
```


### List Shipping Addresses

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8081/customers/<CUSTOMER_ID>/shipping-addresses" `
  -Method Get
```

Expected result:

```text
200 OK
```

The response is a JSON array of shipping addresses.

### Get Shipping Address By ID

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8081/customers/<CUSTOMER_ID>/shipping-addresses/<SHIPPING_ADDRESS_ID>" `
  -Method Get
```

Expected result:

```text
200 OK
```

### Invalid Customer Request

This example sends invalid data.

```powershell
$body = @{
  firstName = ""
  lastName = ""
  email = "not-an-email"
  billingAddress = @{
    street = ""
    city = ""
    postalCode = ""
    country = "D"
  }
} | ConvertTo-Json -Depth 3

Invoke-RestMethod `
  -Uri "http://localhost:8081/customers" `
  -Method Post `
  -ContentType "application/json" `
  -Body $body
```

Expected result:

```text
400 Bad Request
```

The response is a `ProblemDetail` JSON object with validation errors.

### Duplicate Email

Run the valid create customer request twice with the same email:

```text
anna.meyer@example.com
```

Expected result for the second request:

```text
409 Conflict
```

### Unknown Customer

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8081/customers/00000000-0000-0000-0000-000000000000" `
  -Method Get
```

Expected result:

```text
404 Not Found
```

## Automated Test Description

The service contains REST controller tests for the Customer API.

Run tests:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw test
```

### Test: GET /customers

This test calls:

```text
GET /customers
```

The mocked `ListCustomersUseCase` returns an empty list.

Expected result:

```text
200 OK
[]
```

This proves that the list customers endpoint is reachable and returns a JSON response.

### Test: Valid POST /customers

This test sends a valid customer request with first name, last name, email and billing address.

Expected result:

```text
201 Created
```

The `CreateCustomerUseCase` returns a created customer.

The test checks that the response contains:

```text
id
firstName
lastName
email
billingAddress.id
```

This proves that the controller accepts a valid request and maps the created domain customer to the API response correctly.

### Test: Invalid POST /customers

This test sends an invalid customer request:

```text
empty firstName
empty lastName
invalid email
empty billing address fields
invalid country length
```

Expected result:

```text
400 Bad Request
```

## OpenAPI

The API contract is defined in:

```text
src/main/resources/openapi/customer-api.yaml
```

