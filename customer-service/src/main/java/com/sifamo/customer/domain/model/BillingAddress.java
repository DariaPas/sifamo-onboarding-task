package com.sifamo.customer.domain.model;

import java.util.UUID;

public class BillingAddress {

    private final UUID id;
    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;

    public BillingAddress(UUID id, String street, String city, String postalCode, String country) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    public UUID getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }
}