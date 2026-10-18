package com.retailcore.contracts.event;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
public record OrderConfirmed(@JsonProperty("OrderId") int orderId, @JsonProperty("Reference") String reference, @JsonProperty("CustomerEmail") String customerEmail, @JsonProperty("Total") BigDecimal total, @JsonProperty("ConfirmedAtUtc") Instant confirmedAtUtc) {}
