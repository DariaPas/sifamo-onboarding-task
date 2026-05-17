package com.sifamo.customer.application.port.in;

public record CreateCustomerCommand(
        String firstName,
        String lastName,
        String email,
        BillingAddressCommand billingAddress
) {
}