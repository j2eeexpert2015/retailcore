package com.retailcore.contracts.dto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
public record OrderDetailsDto(@JsonProperty("Id") int id, @JsonProperty("Reference") String reference, @JsonProperty("Status") String status, @JsonProperty("CustomerEmail") String customerEmail, @JsonProperty("Subtotal") BigDecimal subtotal, @JsonProperty("DiscountAmount") BigDecimal discountAmount, @JsonProperty("TaxAmount") BigDecimal taxAmount, @JsonProperty("ShippingAmount") BigDecimal shippingAmount, @JsonProperty("Total") BigDecimal total, @JsonProperty("CreatedAtUtc") Instant createdAtUtc, @JsonProperty("EstimatedDeliveryUtc") Instant estimatedDeliveryUtc, @JsonProperty("Lines") List<OrderLineDto> lines) {}
