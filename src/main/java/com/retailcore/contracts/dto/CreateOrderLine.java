package com.retailcore.contracts.dto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
public record CreateOrderLine(@JsonProperty("ProductId") int productId, @JsonProperty("Quantity") int quantity, @JsonProperty("UnitPrice") BigDecimal unitPrice) {}
