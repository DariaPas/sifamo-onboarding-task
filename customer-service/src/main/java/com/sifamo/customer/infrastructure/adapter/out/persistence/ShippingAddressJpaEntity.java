package com.sifamo.customer.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "shipping_addresses", schema = "customer_schema")
public class ShippingAddressJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerJpaEntity customer;

    protected ShippingAddressJpaEntity() {
    }

    public ShippingAddressJpaEntity(UUID id, String street, String city, String postalCode, String country) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    public void setCustomer(CustomerJpaEntity customer) {
        this.customer = customer;
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

    public CustomerJpaEntity getCustomer() {
        return customer;
    }
}