package com.retailcore.tests;

import com.retailcore.orders.repository.OrderRepository;
import com.retailcore.orders.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/** Checks the real H2 application context and seed data, not just compilation. */
@SpringBootTest(properties = "retailcore.runtime-mode=h2")
@ActiveProfiles("h2")
class H2SeedDataIntegrationTest {
    @Autowired OrderRepository orders;
    @Autowired ProductRepository products;

    // H2 startup must seed three orders and seven active products while excluding the discontinued SKU.
    @Test
    void shouldLoadExpectedH2SeedData() {
        assertEquals(3, orders.count(), "Three seeded orders should be available");
        assertEquals(7, products.count(), "Discontinued SKU-1005 should be removed");
        assertTrue(orders.findById(1).isPresent());
        assertTrue(products.findBySku("SKU-1001").isPresent());
        assertTrue(products.findBySku("SKU-1005").isEmpty());
    }
}
