package com.sifamo.order.application.port.out;

import java.util.UUID;

public interface CustomerValidationPort {

    boolean customerExists(UUID customerId);

    boolean shippingAddressExists(UUID customerId, UUID shippingAddressId);
}