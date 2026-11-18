package com.mikael.ecommerce.customer.service;

import com.mikael.ecommerce.customer.dto.CreateCustomerRequest;
import com.mikael.ecommerce.customer.dto.CustomerResponse;
import com.mikael.ecommerce.customer.entity.Customer;
import com.mikael.ecommerce.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(CustomerResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return CustomerResponse.fromEntity(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
        return CustomerResponse.fromEntity(customer);
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmailIgnoreCase(request.getEmail().trim())) {
            throw new IllegalArgumentException("Customer with email already exists: " + request.getEmail());
        }

        Customer customer = Customer.builder()
                .name(request.getName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        Customer saved = customerRepository.save(customer);
        log.info("Created customer: id={}, email={}", saved.getId(), saved.getEmail());
        return CustomerResponse.fromEntity(saved);
    }

    @Transactional
    public CustomerResponse getOrCreateCustomer(CreateCustomerRequest request) {
        Customer customer = customerRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .name(request.getName() != null && !request.getName().isBlank()
                                ? request.getName().trim()
                                : request.getEmail().split("@")[0])
                        .email(request.getEmail().toLowerCase().trim())
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .build()));
        return CustomerResponse.fromEntity(customer);
    }
}
