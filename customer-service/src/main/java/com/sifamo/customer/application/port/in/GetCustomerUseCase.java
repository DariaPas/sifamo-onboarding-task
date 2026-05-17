package com.sifamo.customer.application.port.in;

import com.sifamo.customer.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

public interface GetCustomerUseCase {

    Optional<Customer> getCustomer(UUID id);
}