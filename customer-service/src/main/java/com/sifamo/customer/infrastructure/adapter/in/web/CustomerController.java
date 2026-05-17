package com.sifamo.customer.infrastructure.adapter.in.web;

import com.sifamo.customer.api.DefaultApi;
import com.sifamo.customer.api.model.BillingAddress;
import com.sifamo.customer.api.model.Customer;
import com.sifamo.customer.api.model.CreateCustomerRequest;
import com.sifamo.customer.api.model.CreateShippingAddressRequest;
import com.sifamo.customer.api.model.ShippingAddress;
import com.sifamo.customer.application.port.in.AddShippingAddressCommand;
import com.sifamo.customer.application.port.in.AddShippingAddressUseCase;
import com.sifamo.customer.application.port.in.BillingAddressCommand;
import com.sifamo.customer.application.port.in.CreateCustomerCommand;
import com.sifamo.customer.application.port.in.CreateCustomerUseCase;
import com.sifamo.customer.application.port.in.GetCustomerUseCase;
import com.sifamo.customer.application.port.in.GetShippingAddressUseCase;
import com.sifamo.customer.application.port.in.ListCustomersUseCase;
import com.sifamo.customer.application.port.in.ListShippingAddressesUseCase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CustomerController implements DefaultApi {
	
	 private final CreateCustomerUseCase createCustomerUseCase;
	 private final ListCustomersUseCase listCustomersUseCase;
	 private final GetCustomerUseCase getCustomerUseCase;
	 
	 private final AddShippingAddressUseCase addShippingAddressUseCase;
	 private final ListShippingAddressesUseCase listShippingAddressesUseCase;
	 private final GetShippingAddressUseCase getShippingAddressUseCase;
	 
	
	public CustomerController(CreateCustomerUseCase createCustomerUseCase,
            ListCustomersUseCase listCustomersUseCase,
            GetCustomerUseCase getCustomerUseCase,
            AddShippingAddressUseCase addShippingAddressUseCase,
            ListShippingAddressesUseCase listShippingAddressesUseCase,
            GetShippingAddressUseCase getShippingAddressUseCase
    ) {
		this.createCustomerUseCase = createCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        
        this.addShippingAddressUseCase = addShippingAddressUseCase;
        this.listShippingAddressesUseCase = listShippingAddressesUseCase;
        this.getShippingAddressUseCase = getShippingAddressUseCase;
    }

    @Override
    public ResponseEntity<List<Customer>> customersGet() {
    	List<Customer> customers = listCustomersUseCase.listCustomers()
                .stream()
                .map(this::toApiCustomer)
                .toList();

        return ResponseEntity.ok(customers);
    }

    @Override
    public ResponseEntity<Customer> customersPost(CreateCustomerRequest request) {
    	CreateCustomerCommand command = new CreateCustomerCommand(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                new BillingAddressCommand(
                        request.getBillingAddress().getStreet(),
                        request.getBillingAddress().getCity(),
                        request.getBillingAddress().getPostalCode(),
                        request.getBillingAddress().getCountry()
                )
        );

        com.sifamo.customer.domain.model.Customer createdCustomer = createCustomerUseCase.createCustomer(command);

        return ResponseEntity.status(201).body(toApiCustomer(createdCustomer));
    }

    @Override
    public ResponseEntity<Customer> customersIdGet(UUID id) {
    	return getCustomerUseCase.getCustomer(id)
                .map(customer -> ResponseEntity.ok(toApiCustomer(customer)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<ShippingAddress>> customersIdShippingAddressesGet(UUID id) {
    	List<ShippingAddress> shippingAddresses = listShippingAddressesUseCase.listShippingAddresses(id)
                .stream()
                .map(this::toApiShippingAddress)
                .toList();

        return ResponseEntity.ok(shippingAddresses);
    }

    @Override
    public ResponseEntity<ShippingAddress> customersIdShippingAddressesPost(
            UUID id,
            CreateShippingAddressRequest request
    ) {
    	AddShippingAddressCommand command = new AddShippingAddressCommand(
                request.getStreet(),
                request.getCity(),
                request.getPostalCode(),
                request.getCountry()
        );

        com.sifamo.customer.domain.model.ShippingAddress createdShippingAddress =
                addShippingAddressUseCase.addShippingAddress(id, command);

        return ResponseEntity.status(201).body(toApiShippingAddress(createdShippingAddress));
    }

    @Override
    public ResponseEntity<ShippingAddress> customersIdShippingAddressesShippingAddressIdGet(
            UUID id,
            UUID shippingAddressId
    ) {
    	return getShippingAddressUseCase.getShippingAddress(id, shippingAddressId)
                .map(shippingAddress -> ResponseEntity.ok(toApiShippingAddress(shippingAddress)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    private Customer toApiCustomer(com.sifamo.customer.domain.model.Customer domainCustomer) {
        Customer apiCustomer = new Customer();
        apiCustomer.setId(domainCustomer.getId());
        apiCustomer.setFirstName(domainCustomer.getFirstName());
        apiCustomer.setLastName(domainCustomer.getLastName());
        apiCustomer.setEmail(domainCustomer.getEmail());

        com.sifamo.customer.domain.model.BillingAddress domainBillingAddress = domainCustomer.getBillingAddress();

        BillingAddress apiBillingAddress = new BillingAddress();
        apiBillingAddress.setId(domainBillingAddress.getId());
        apiBillingAddress.setStreet(domainBillingAddress.getStreet());
        apiBillingAddress.setCity(domainBillingAddress.getCity());
        apiBillingAddress.setPostalCode(domainBillingAddress.getPostalCode());
        apiBillingAddress.setCountry(domainBillingAddress.getCountry());

        apiCustomer.setBillingAddress(apiBillingAddress);

        return apiCustomer;
    }
    
    private ShippingAddress toApiShippingAddress(
            com.sifamo.customer.domain.model.ShippingAddress domainShippingAddress
    ) {
        ShippingAddress apiShippingAddress = new ShippingAddress();
        apiShippingAddress.setId(domainShippingAddress.getId());
        apiShippingAddress.setStreet(domainShippingAddress.getStreet());
        apiShippingAddress.setCity(domainShippingAddress.getCity());
        apiShippingAddress.setPostalCode(domainShippingAddress.getPostalCode());
        apiShippingAddress.setCountry(domainShippingAddress.getCountry());

        return apiShippingAddress;
    }
}