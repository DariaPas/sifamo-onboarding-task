package com.sifamo.customer.infrastructure.adapter.out.persistence;

import com.sifamo.customer.application.port.out.CustomerRepositoryPort;
import com.sifamo.customer.domain.exception.CustomerNotFoundException;
import com.sifamo.customer.domain.model.BillingAddress;
import com.sifamo.customer.domain.model.Customer;
import com.sifamo.customer.domain.model.ShippingAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final SpringDataCustomerRepository springDataCustomerRepository;
    private final SpringDataShippingAddressRepository springDataShippingAddressRepository;

    public CustomerPersistenceAdapter(SpringDataCustomerRepository springDataCustomerRepository,
    		SpringDataShippingAddressRepository springDataShippingAddressRepository) {
        this.springDataCustomerRepository = springDataCustomerRepository;
        this.springDataShippingAddressRepository = springDataShippingAddressRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity entity = toJpaEntity(customer);
        CustomerJpaEntity savedEntity = springDataCustomerRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<Customer> findAll() {
        return springDataCustomerRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return springDataCustomerRepository.findById(id)
                .map(this::toDomain);
    }
    
    
    @Override
    public ShippingAddress saveShippingAddress(UUID customerId, ShippingAddress shippingAddress) {
        CustomerJpaEntity customerEntity = springDataCustomerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        ShippingAddressJpaEntity shippingAddressEntity = toJpaEntity(shippingAddress);
        shippingAddressEntity.setCustomer(customerEntity);

        ShippingAddressJpaEntity savedEntity = springDataShippingAddressRepository.save(shippingAddressEntity);

        return toDomain(savedEntity);
    }
    
    @Override
    public Optional<ShippingAddress> findShippingAddressById(UUID customerId, UUID shippingAddressId) {
        return springDataShippingAddressRepository.findByIdAndCustomerId(shippingAddressId, customerId)
                .map(this::toDomain);
    }
    
    @Override
    public List<ShippingAddress> findShippingAddressesByCustomerId(UUID customerId) {
        return springDataShippingAddressRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toDomain)
                .toList();
    }
    

    private CustomerJpaEntity toJpaEntity(Customer customer) {
        CustomerJpaEntity customerEntity = new CustomerJpaEntity(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );

        BillingAddress billingAddress = customer.getBillingAddress();

        BillingAddressJpaEntity billingAddressEntity = new BillingAddressJpaEntity(
                billingAddress.getId(),
                billingAddress.getStreet(),
                billingAddress.getCity(),
                billingAddress.getPostalCode(),
                billingAddress.getCountry()
        );

        customerEntity.setBillingAddress(billingAddressEntity);

        return customerEntity;
    }
    
    private ShippingAddressJpaEntity toJpaEntity(ShippingAddress shippingAddress) {
        return new ShippingAddressJpaEntity(
                shippingAddress.getId(),
                shippingAddress.getStreet(),
                shippingAddress.getCity(),
                shippingAddress.getPostalCode(),
                shippingAddress.getCountry()
        );
    }
    

    private Customer toDomain(CustomerJpaEntity entity) {
        BillingAddressJpaEntity billingEntity = entity.getBillingAddress();

        BillingAddress billingAddress = new BillingAddress(
                billingEntity.getId(),
                billingEntity.getStreet(),
                billingEntity.getCity(),
                billingEntity.getPostalCode(),
                billingEntity.getCountry()
        );

        return new Customer(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                billingAddress
        );
    }
    
    private ShippingAddress toDomain(ShippingAddressJpaEntity entity) {
        return new ShippingAddress(
                entity.getId(),
                entity.getStreet(),
                entity.getCity(),
                entity.getPostalCode(),
                entity.getCountry()
        );
    }
}