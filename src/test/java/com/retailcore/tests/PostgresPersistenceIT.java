package com.retailcore.tests;

import com.retailcore.orders.domain.Product;
import com.retailcore.orders.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** Runs only with -Ppostgres-tests; requires a functioning Docker daemon. */
@Testcontainers
@SpringBootTest(properties = {"retailcore.runtime-mode=postgres",
        "spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never"})
@ActiveProfiles("postgres")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PostgresPersistenceIT {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired ProductRepository products;

    // A real PostgreSQL schema must persist and retrieve a product through Spring Data JPA.
    @Test
    void shouldPersistAndRetrieveProductInPostgres() {
        Product product = new Product();
        product.sku = "INTEGRATION-SKU";
        product.name = "Integration test item";
        product.category = "Test";
        product.unitPrice = new BigDecimal("12.50");
        product.weightKg = new BigDecimal("0.100");
        Product saved = products.saveAndFlush(product);
        assertEquals("INTEGRATION-SKU", products.findById(saved.id).orElseThrow().sku);
    }
}
