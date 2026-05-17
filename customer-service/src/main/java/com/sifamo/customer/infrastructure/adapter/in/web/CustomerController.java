package com.sifamo.customer.infrastructure.adapter.in.web;

import com.sifamo.customer.api.DefaultApi;
import com.sifamo.customer.api.model.BillingAddress;
import com.sifamo.customer.api.model.Customer;
import com.sifamo.customer.api.model.CreateCustomerRequest;
import com.sifamo.customer.api.model.CreateShippingAddressRequest;
import com.sifamo.customer.api.model.ShippingAddress;
import com.sifamo.customer.infrastructure.adapter.out.persistence.BillingAddressJpaEntity;
import com.sifamo.customer.infrastructure.adapter.out.persistence.CustomerJpaEntity;
import com.sifamo.customer.infrastructure.adapter.out.persistence.SpringDataCustomerRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CustomerController implements DefaultApi {
	
	private final SpringDataCustomerRepository customerRepository;
	
	public CustomerController(SpringDataCustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public ResponseEntity<List<Customer>> customersGet() {
    	List<Customer> customers = customerRepository.findAll()
                .stream()
                .map(this::toApiCustomer)
                .toList();

        return ResponseEntity.ok(customers);
    }

    @Override
    public ResponseEntity<Customer> customersPost(CreateCustomerRequest request) {
    	UUID customerId = UUID.randomUUID();
        UUID billingAddressId = UUID.randomUUID();

        CustomerJpaEntity customerEntity = new CustomerJpaEntity(
                customerId,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail()
        );

        BillingAddressJpaEntity billingAddressEntity = new BillingAddressJpaEntity(
                billingAddressId,
                request.getBillingAddress().getStreet(),
                request.getBillingAddress().getCity(),
                request.getBillingAddress().getPostalCode(),
                request.getBillingAddress().getCountry()
        );

        customerEntity.setBillingAddress(billingAddressEntity);

        CustomerJpaEntity savedCustomer = customerRepository.save(customerEntity);

        return ResponseEntity.status(201).body(toApiCustomer(savedCustomer));
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
    
    private Customer toApiCustomer(CustomerJpaEntity entity) {
        Customer customer = new Customer();
        customer.setId(entity.getId());
        customer.setFirstName(entity.getFirstName());
        customer.setLastName(entity.getLastName());
        customer.setEmail(entity.getEmail());

        BillingAddressJpaEntity billingEntity = entity.getBillingAddress();

        BillingAddress billingAddress = new BillingAddress();
        billingAddress.setId(billingEntity.getId());
        billingAddress.setStreet(billingEntity.getStreet());
        billingAddress.setCity(billingEntity.getCity());
        billingAddress.setPostalCode(billingEntity.getPostalCode());
        billingAddress.setCountry(billingEntity.getCountry());

        customer.setBillingAddress(billingAddress);

        return customer;
    }
}