package com.sifamo.customer.application.port.in;

import com.sifamo.customer.domain.model.ShippingAddress;

import java.util.UUID;

public interface AddShippingAddressUseCase {

    ShippingAddress addShippingAddress(UUID customerId, AddShippingAddressCommand command);
}