package com.retailcore.contracts.dto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
public record PaymentChargeRequest(@JsonProperty("OrderReference") String orderReference, @JsonProperty("CustomerEmail") String customerEmail, @JsonProperty("Amount") BigDecimal amount, @JsonProperty("Currency") String currency, @JsonProperty("CardNumber") String cardNumber) {}
