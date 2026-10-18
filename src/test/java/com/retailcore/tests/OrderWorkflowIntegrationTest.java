package com.retailcore.tests;

import com.retailcore.contracts.dto.*;
import com.retailcore.orders.repository.*;
import com.retailcore.orders.service.*;
import com.retailcore.payments.service.PaymentsClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Uses real H2 persistence and a deterministic fake payment response.
 * Does not assert behavior that the teaching tickets intentionally leave broken.
 */
@SpringBootTest(properties = "retailcore.runtime-mode=h2")
@ActiveProfiles("h2")
@Transactional
class OrderWorkflowIntegrationTest {
    @Autowired OrderRepository orders;
    @Autowired OrderLineRepository lines;
    @Autowired ProductRepository products;
    @Autowired StockService stock;

    private OrderManager managerWithPaymentStatus(String status) {
        PaymentsClient client = mock(PaymentsClient.class);
        when(client.charge(any(PaymentChargeRequest.class)))
                .thenReturn(new PaymentResult("test-payment-123", status, "test", Instant.now()));
        return new OrderManager(orders, lines, products, stock, client, "USD");
    }

    private CreateOrderRequest orderRequest() {
        return new CreateOrderRequest("TEST@EXAMPLE.COM", "CA",
                List.of(new CreateOrderLine(1, 1, null)), null, "4242424242424242");
    }

    // Approved payment must confirm the order, normalize the email and reserve the purchased stock.
    @Test
    void shouldConfirmPaidOrderAndReserveStock() {
        var manager = managerWithPaymentStatus("Approved");
        int initialStock = stock.getAvailable(1);
        var outcome = manager.createOrder(orderRequest());

        assertTrue(outcome.success());
        assertEquals("Confirmed", outcome.order().status());
        assertEquals("test@example.com", outcome.order().customerEmail());
        assertEquals(initialStock - 1, stock.getAvailable(1));
        var details = manager.getOrderDetails(outcome.order().id());
        assertNotNull(details);
        assertEquals(1, details.lines().size());
        assertEquals(0, new BigDecimal("49.90").compareTo(details.subtotal()));
    }

    // Declined payment must mark the order declined and restore any stock reserved for it.
    @Test
    void shouldDeclinePaymentAndRestoreStock() {
        var manager = managerWithPaymentStatus("Declined");
        int initialStock = stock.getAvailable(1);
        var outcome = manager.createOrder(orderRequest());

        assertTrue(outcome.success());
        assertEquals("Declined", outcome.order().status());
        assertEquals(initialStock, stock.getAvailable(1));
    }

    // Insufficient inventory must reject the purchase without creating an order record.
    @Test
    void shouldRejectOutOfStockOrderWithoutPersistence() {
        var manager = managerWithPaymentStatus("Approved");
        long initialOrders = orders.count();
        var request = new CreateOrderRequest("buyer@example.com", "CA",
                List.of(new CreateOrderLine(7, 100000, null)), null, "4242424242424242");
        var outcome = manager.createOrder(request);

        assertFalse(outcome.success());
        assertEquals(initialOrders, orders.count());
    }
}
