package com.mikael.ecommerce.customer.config;

import com.mikael.ecommerce.customer.entity.Customer;
import com.mikael.ecommerce.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CustomerDataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            log.info("Customers already seeded.");
            return;
        }

        Customer alice = Customer.builder()
                .name("Alice Johnson")
                .email("alice.johnson@example.com")
                .phone("08123456789")
                .address("Jl. Sudirman No. 10, Jakarta")
                .build();

        Customer bob = Customer.builder()
                .name("Bob Smith")
                .email("bob.smith@example.com")
                .phone("08129876543")
                .address("Jl. Gatot Subroto No. 45, Bandung")
                .build();

        Customer charlie = Customer.builder()
                .name("Charlie Brown")
                .email("charlie.brown@example.com")
                .phone("08125556677")
                .address("Jl. Diponegoro No. 8, Surabaya")
                .build();

        customerRepository.saveAll(List.of(alice, bob, charlie));
        log.info("Successfully seeded 3 customers in customer-service.");
    }
}
