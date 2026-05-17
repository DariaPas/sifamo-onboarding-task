package com.sifamo.customer.application.port.in;

public record BillingAddressCommand(
        String street,
        String city,
        String postalCode,
        String country
) {
}