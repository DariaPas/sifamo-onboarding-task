package com.sifamo.order.infrastructure.adapter.out.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {
	
	List<OrderJpaEntity> findByCustomerId(UUID customerId);

}
