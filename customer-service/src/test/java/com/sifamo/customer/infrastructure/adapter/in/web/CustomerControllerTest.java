package com.sifamo.customer.infrastructure.adapter.in.web;

import com.sifamo.customer.application.port.in.AddShippingAddressUseCase;
import com.sifamo.customer.application.port.in.CreateCustomerCommand;
import com.sifamo.customer.application.port.in.CreateCustomerUseCase;
import com.sifamo.customer.application.port.in.GetCustomerUseCase;
import com.sifamo.customer.application.port.in.GetShippingAddressUseCase;
import com.sifamo.customer.application.port.in.ListCustomersUseCase;
import com.sifamo.customer.application.port.in.ListShippingAddressesUseCase;
import com.sifamo.customer.domain.model.BillingAddress;
import com.sifamo.customer.domain.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCustomerUseCase createCustomerUseCase;

    @MockitoBean
    private ListCustomersUseCase listCustomersUseCase;

    @MockitoBean
    private GetCustomerUseCase getCustomerUseCase;

    @MockitoBean
    private AddShippingAddressUseCase addShippingAddressUseCase;

    @MockitoBean
    private ListShippingAddressesUseCase listShippingAddressesUseCase;

    @MockitoBean
    private GetShippingAddressUseCase getShippingAddressUseCase;

    @Test
    void customersGetReturnsOk() throws Exception {
        when(listCustomersUseCase.listCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/customers"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void customersPostWithValidBodyReturnsCreatedCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID billingAddressId = UUID.randomUUID();

        BillingAddress billingAddress = new BillingAddress(
                billingAddressId,
                "Example Street 10",
                "Hamburg",
                "20095",
                "DE"
        );

        Customer customer = new Customer(
                customerId,
                "Anna",
                "Meyer",
                "anna.meyer.test@example.com",
                billingAddress
        );

        when(createCustomerUseCase.createCustomer(any(CreateCustomerCommand.class)))
                .thenReturn(customer);

        String body = """
                {
                  "firstName": "Anna",
                  "lastName": "Meyer",
                  "email": "anna.meyer.test@example.com",
                  "billingAddress": {
                    "street": "Example Street 10",
                    "city": "Hamburg",
                    "postalCode": "20095",
                    "country": "DE"
                  }
                }
                """;

        mockMvc.perform(post("/customers")
                        .contentType("application/json")
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.firstName").value("Anna"))
                .andExpect(jsonPath("$.lastName").value("Meyer"))
                .andExpect(jsonPath("$.email").value("anna.meyer.test@example.com"))
                .andExpect(jsonPath("$.billingAddress.id").value(billingAddressId.toString()));
    }

    @Test
    void customersPostWithInvalidBodyReturnsBadRequest() throws Exception {
        String body = """
                {
                  "firstName": "",
                  "lastName": "",
                  "email": "not-an-email",
                  "billingAddress": {
                    "street": "",
                    "city": "",
                    "postalCode": "",
                    "country": "D"
                  }
                }
                """;

        mockMvc.perform(post("/customers")
                        .contentType("application/json")
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}