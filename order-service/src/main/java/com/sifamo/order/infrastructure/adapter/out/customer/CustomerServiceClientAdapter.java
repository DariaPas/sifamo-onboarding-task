package com.sifamo.order.infrastructure.adapter.out.customer;

import com.sifamo.order.application.port.out.CustomerValidationPort;
import com.sifamo.order.client.customer.ApiClient;
import com.sifamo.order.client.customer.api.CustomersApi;
import com.sifamo.order.client.customer.api.ShippingAddressesApi;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.UUID;

@Component
public class CustomerServiceClientAdapter implements CustomerValidationPort {

	private final CustomersApi customersApi;
	private final ShippingAddressesApi shippingAddressesApi;

    public CustomerServiceClientAdapter() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://localhost:8081");

        this.customersApi = new CustomersApi(apiClient);
        this.shippingAddressesApi = new ShippingAddressesApi(apiClient);
    }

    @Override
    public boolean customerExists(UUID customerId) {
        try {
            customersApi.customersIdGet(customerId);
            return true;
        } catch (HttpClientErrorException.NotFound exception) {
            return false;
        }
    }

    @Override
    public boolean shippingAddressExists(UUID customerId, UUID shippingAddressId) {
        try {
        	shippingAddressesApi.customersIdShippingAddressesShippingAddressIdGet(customerId, shippingAddressId);
            return true;
        } catch (HttpClientErrorException.NotFound exception) {
            return false;
        }
    }
}