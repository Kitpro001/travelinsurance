package com.example.travelinsurance.repository;

import com.example.travelinsurance.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNric(String nric);
}
