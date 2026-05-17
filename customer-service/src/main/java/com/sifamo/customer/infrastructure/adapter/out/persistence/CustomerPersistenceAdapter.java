package com.sifamo.customer.infrastructure.adapter.out.persistence;

import com.sifamo.customer.application.port.out.CustomerRepositoryPort;
import com.sifamo.customer.domain.model.BillingAddress;
import com.sifamo.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final SpringDataCustomerRepository springDataCustomerRepository;

    public CustomerPersistenceAdapter(SpringDataCustomerRepository springDataCustomerRepository) {
        this.springDataCustomerRepository = springDataCustomerRepository;
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
}