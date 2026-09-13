package com.marcosperboni.customerservice.service;

import com.marcosperboni.customerservice.dto.CustomerDto;
import com.marcosperboni.customerservice.dto.CustomerRequest;
import com.marcosperboni.customerservice.entity.Customer;
import com.marcosperboni.customerservice.exception.ResourceNotFoundException;
import com.marcosperboni.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerService service() {
        return new CustomerService(customerRepository);
    }

    @Test
    void findByIdReturnsDtoWhenCustomerExists() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName("Ana");
        customer.setEmail("ana@example.com");
        customer.setCreatedAt(Instant.now());
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        CustomerDto dto = service().findById(id);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.name()).isEqualTo("Ana");
        assertThat(dto.email()).isEqualTo("ana@example.com");
    }

    @Test
    void findByIdThrowsWhenCustomerMissing() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createSavesAndReturnsDto() {
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setId(UUID.randomUUID());
            c.setCreatedAt(Instant.now());
            return c;
        });

        CustomerDto dto = service().create(new CustomerRequest("Bob", "bob@example.com", "123", "Street 1"));

        assertThat(dto.id()).isNotNull();
        assertThat(dto.name()).isEqualTo("Bob");
        assertThat(dto.email()).isEqualTo("bob@example.com");
    }
}
