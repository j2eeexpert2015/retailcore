package com.retailcore.orders.repository;
import com.retailcore.orders.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Integer> { java.util.Optional<Product> findBySku(String sku); }
