package com.sifamo.customer.application.port.in;

import com.sifamo.customer.domain.model.Customer;

public interface CreateCustomerUseCase {

    Customer createCustomer(CreateCustomerCommand command);
}