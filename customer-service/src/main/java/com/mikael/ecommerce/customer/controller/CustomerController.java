package com.mikael.ecommerce.customer.controller;

import com.mikael.ecommerce.customer.dto.ApiResponse;
import com.mikael.ecommerce.customer.dto.CreateCustomerRequest;
import com.mikael.ecommerce.customer.dto.CustomerResponse;
import com.mikael.ecommerce.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable Long id) {
        CustomerResponse customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success("Customer found", customer));
    }

    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByEmail(@RequestParam String email) {
        CustomerResponse customer = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Customer found", customer));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse created = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully", created));
    }

    @PostMapping("/get-or-create")
    public ResponseEntity<ApiResponse<CustomerResponse>> getOrCreateCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse customer = customerService.getOrCreateCustomer(request);
        return ResponseEntity.ok(ApiResponse.success("Customer resolved", customer));
    }
}
