package com.sifamo.customer.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataShippingAddressRepository extends JpaRepository<ShippingAddressJpaEntity, UUID> {

    List<ShippingAddressJpaEntity> findByCustomerId(UUID customerId);

    Optional<ShippingAddressJpaEntity> findByIdAndCustomerId(UUID id, UUID customerId);
}