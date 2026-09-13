package com.marcosperboni.customerservice.service;

import com.marcosperboni.customerservice.dto.CustomerDto;
import com.marcosperboni.customerservice.dto.CustomerRequest;
import com.marcosperboni.customerservice.entity.Customer;
import com.marcosperboni.customerservice.exception.ResourceNotFoundException;
import com.marcosperboni.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDto> findAll() {
        return customerRepository.findAll().stream().map(CustomerService::toDto).toList();
    }

    public CustomerDto findById(UUID id) {
        return toDto(getOrThrow(id));
    }

    public CustomerDto create(CustomerRequest request) {
        Customer customer = new Customer();
        applyRequest(customer, request);
        return toDto(customerRepository.save(customer));
    }

    public CustomerDto update(UUID id, CustomerRequest request) {
        Customer customer = getOrThrow(id);
        applyRequest(customer, request);
        return toDto(customerRepository.save(customer));
    }

    public void delete(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found: " + id);
        }
        customerRepository.deleteById(id);
    }

    private Customer getOrThrow(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
    }

    private static void applyRequest(Customer customer, CustomerRequest request) {
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());
    }

    private static CustomerDto toDto(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCreatedAt()
        );
    }
}
