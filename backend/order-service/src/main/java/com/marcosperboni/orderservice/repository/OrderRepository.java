package com.marcosperboni.orderservice.repository;

import com.marcosperboni.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

	List<Order> findByCustomerId(UUID customerId);
}
