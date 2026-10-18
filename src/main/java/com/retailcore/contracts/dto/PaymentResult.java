package com.retailcore.contracts.dto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
public record PaymentResult(@JsonProperty("PaymentId") String paymentId, @JsonProperty("Status") String status, @JsonProperty("Message") String message, @JsonProperty("ProcessedAtUtc") Instant processedAtUtc) {}
