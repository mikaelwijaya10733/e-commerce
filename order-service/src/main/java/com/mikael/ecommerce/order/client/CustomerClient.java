package com.mikael.ecommerce.order.client;

import com.mikael.ecommerce.order.dto.ApiResponse;
import com.mikael.ecommerce.order.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerClient {

    private final RestClient customerRestClient;

    public CustomerResponse getCustomerById(Long customerId) {
        log.info("Calling customer-service to validate customer id: {}", customerId);
        try {
            ApiResponse<CustomerResponse> response = customerRestClient.get()
                    .uri("/api/customers/{id}", customerId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new RuntimeException("Customer not found or error in customer-service. Status: " + res.getStatusCode());
                    })
                    .body(new ParameterizedTypeReference<ApiResponse<CustomerResponse>>() {});

            if (response == null || response.getData() == null) {
                throw new RuntimeException("Customer not found with id: " + customerId);
            }
            return response.getData();
        } catch (Exception e) {
            log.error("Error communicating with customer-service for customerId {}: {}", customerId, e.getMessage());
            throw new RuntimeException("Cannot resolve customer: " + e.getMessage(), e);
        }
    }
}
