package com.retailcore.orders.repository;
import com.retailcore.orders.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order, Integer> { java.util.List<Order> findTop100ByOrderByCreatedAtUtcDesc(); }
