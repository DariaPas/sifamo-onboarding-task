package com.sifamo.customer.application.port.in;

import com.sifamo.customer.domain.model.ShippingAddress;

import java.util.List;
import java.util.UUID;

public interface ListShippingAddressesUseCase {

    List<ShippingAddress> listShippingAddresses(UUID customerId);
}