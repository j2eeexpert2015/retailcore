package com.retailcore.contracts.dto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
public record CreateOrderRequest(@JsonProperty("CustomerEmail") String customerEmail, @JsonProperty("ShippingRegion") String shippingRegion, @JsonProperty("Lines") List<CreateOrderLine> lines, @JsonProperty("Total") BigDecimal total, @JsonProperty("CardNumber") String cardNumber) {}
