package com.sifamo.customer.application.port.in;

import com.sifamo.customer.domain.model.ShippingAddress;

import java.util.Optional;
import java.util.UUID;

public interface GetShippingAddressUseCase {

    Optional<ShippingAddress> getShippingAddress(UUID customerId, UUID shippingAddressId);
}