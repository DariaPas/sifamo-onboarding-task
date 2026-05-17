package com.sifamo.customer.domain.model;

import java.util.UUID;

public class Customer {

    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final BillingAddress billingAddress;

    public Customer(UUID id, String firstName, String lastName, String email, BillingAddress billingAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.billingAddress = billingAddress;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public BillingAddress getBillingAddress() {
        return billingAddress;
    }
}