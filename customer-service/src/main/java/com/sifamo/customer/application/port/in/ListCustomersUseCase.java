package com.sifamo.customer.application.port.in;

import com.sifamo.customer.domain.model.Customer;

import java.util.List;

public interface ListCustomersUseCase {

    List<Customer> listCustomers();
}