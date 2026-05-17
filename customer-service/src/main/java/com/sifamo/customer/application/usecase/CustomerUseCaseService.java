package com.sifamo.customer.application.usecase;

import com.sifamo.customer.application.port.in.BillingAddressCommand;
import com.sifamo.customer.application.port.in.CreateCustomerCommand;
import com.sifamo.customer.application.port.in.CreateCustomerUseCase;
import com.sifamo.customer.application.port.in.GetCustomerUseCase;
import com.sifamo.customer.application.port.in.ListCustomersUseCase;
import com.sifamo.customer.application.port.out.CustomerRepositoryPort;
import com.sifamo.customer.domain.model.BillingAddress;
import com.sifamo.customer.domain.model.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerUseCaseService implements CreateCustomerUseCase, ListCustomersUseCase, GetCustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;

    public CustomerUseCaseService(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }

    @Override
    @Transactional
    public Customer createCustomer(CreateCustomerCommand command) {
        UUID customerId = UUID.randomUUID();
        UUID billingAddressId = UUID.randomUUID();

        BillingAddressCommand billingCommand = command.billingAddress();

        BillingAddress billingAddress = new BillingAddress(
                billingAddressId,
                billingCommand.street(),
                billingCommand.city(),
                billingCommand.postalCode(),
                billingCommand.country()
        );

        Customer customer = new Customer(
                customerId,
                command.firstName(),
                command.lastName(),
                command.email(),
                billingAddress
        );

        return customerRepositoryPort.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> listCustomers() {
        return customerRepositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomer(UUID id) {
        return customerRepositoryPort.findById(id);
    }
}