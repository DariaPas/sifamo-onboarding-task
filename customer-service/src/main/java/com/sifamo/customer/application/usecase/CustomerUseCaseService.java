package com.sifamo.customer.application.usecase;

import com.sifamo.customer.application.port.in.AddShippingAddressCommand;
import com.sifamo.customer.application.port.in.AddShippingAddressUseCase;
import com.sifamo.customer.application.port.in.BillingAddressCommand;
import com.sifamo.customer.application.port.in.CreateCustomerCommand;
import com.sifamo.customer.application.port.in.CreateCustomerUseCase;
import com.sifamo.customer.application.port.in.GetCustomerUseCase;
import com.sifamo.customer.application.port.in.GetShippingAddressUseCase;
import com.sifamo.customer.application.port.in.ListCustomersUseCase;
import com.sifamo.customer.application.port.in.ListShippingAddressesUseCase;
import com.sifamo.customer.application.port.out.CustomerRepositoryPort;
import com.sifamo.customer.domain.model.BillingAddress;
import com.sifamo.customer.domain.model.Customer;
import com.sifamo.customer.domain.model.ShippingAddress;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerUseCaseService implements 
											CreateCustomerUseCase, 
											ListCustomersUseCase, 
											GetCustomerUseCase, 
											AddShippingAddressUseCase,
											ListShippingAddressesUseCase,
											GetShippingAddressUseCase {

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
    
    
    @Override
    @Transactional
    public ShippingAddress addShippingAddress(UUID customerId, AddShippingAddressCommand command) {
        ShippingAddress shippingAddress = new ShippingAddress(
                UUID.randomUUID(),
                command.street(),
                command.city(),
                command.postalCode(),
                command.country()
        );

        return customerRepositoryPort.saveShippingAddress(customerId, shippingAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingAddress> listShippingAddresses(UUID customerId) {
        return customerRepositoryPort.findShippingAddressesByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShippingAddress> getShippingAddress(UUID customerId, UUID shippingAddressId) {
        return customerRepositoryPort.findShippingAddressById(customerId, shippingAddressId);
    }
}