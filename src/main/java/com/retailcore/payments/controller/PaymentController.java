package com.retailcore.payments.controller;

import com.retailcore.contracts.dto.PaymentChargeRequest;
import com.retailcore.contracts.dto.PaymentResult;
import com.retailcore.payments.service.PaymentsClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    private final PaymentsClient client;
    public PaymentController(PaymentsClient client) { this.client = client; }
    @PostMapping("/payments/charge")
    public PaymentResult charge(@RequestBody PaymentChargeRequest request) {
        return client.charge(request);
    }
}
