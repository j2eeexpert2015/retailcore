package com.retailcore.orders.repository;
import com.retailcore.orders.domain.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderLineRepository extends JpaRepository<OrderLine, Integer> { java.util.List<OrderLine> findByOrderIdOrderByIdAsc(int orderId); }
