package com.sifamo.customer.infrastructure.adapter.in.web;

import com.sifamo.customer.api.DefaultApi;
import com.sifamo.customer.api.model.Customer;
import com.sifamo.customer.api.model.CreateCustomerRequest;
import com.sifamo.customer.api.model.CreateShippingAddressRequest;
import com.sifamo.customer.api.model.ShippingAddress;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CustomerController implements DefaultApi {

    @Override
    public ResponseEntity<List<Customer>> customersGet() {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<Customer> customersPost(CreateCustomerRequest createCustomerRequest) {
        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<Customer> customersIdGet(UUID id) {
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<ShippingAddress>> customersIdShippingAddressesGet(UUID id) {
        return ResponseEntity.ok(List.of());
    }

    @Override
    public ResponseEntity<ShippingAddress> customersIdShippingAddressesPost(
            UUID id,
            CreateShippingAddressRequest createShippingAddressRequest
    ) {
        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<ShippingAddress> customersIdShippingAddressesShippingAddressIdGet(
            UUID id,
            UUID shippingAddressId
    ) {
        return ResponseEntity.notFound().build();
    }
}