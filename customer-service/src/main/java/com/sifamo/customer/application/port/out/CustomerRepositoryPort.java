package com.sifamo.customer.application.port.out;

import com.sifamo.customer.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepositoryPort {

    Customer save(Customer customer);

    List<Customer> findAll();

    Optional<Customer> findById(UUID id);
}