package com.retailcore.contracts.dto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
public record OrderLineDto(@JsonProperty("ProductId") int productId, @JsonProperty("Sku") String sku, @JsonProperty("ProductName") String productName, @JsonProperty("Category") String category, @JsonProperty("Quantity") int quantity, @JsonProperty("UnitPrice") BigDecimal unitPrice, @JsonProperty("LineTotal") BigDecimal lineTotal) {}
