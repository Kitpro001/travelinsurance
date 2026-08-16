package com.example.travelinsurance.service;

import com.example.travelinsurance.dto.CustomerRequest;
import com.example.travelinsurance.entity.Customer;

public interface CustomerService {

    Customer findOrCreateCustomer(CustomerRequest request);
}