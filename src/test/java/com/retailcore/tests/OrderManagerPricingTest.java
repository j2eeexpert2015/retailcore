package com.retailcore.tests;

import com.retailcore.orders.service.OrderManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderManagerPricingTest {

    // Currency amounts must round HALF_UP to two decimals, including the half-cent boundary.
    @Test
    void shouldRoundCurrencyToTwoDecimalPlacesUsingHalfUp() {
        assertEquals(new BigDecimal("10.01"), OrderManager.roundCurrency(new BigDecimal("10.005")));
        assertEquals(new BigDecimal("10.00"), OrderManager.roundCurrency(new BigDecimal("10.004")));
        assertEquals(new BigDecimal("0.00"), OrderManager.roundCurrency(BigDecimal.ZERO));
    }
}
