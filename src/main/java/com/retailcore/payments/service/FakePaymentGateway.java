package com.retailcore.payments.service;
import com.retailcore.contracts.dto.PaymentChargeRequest;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
@Service public class FakePaymentGateway {
 public GatewayReceipt charge(PaymentChargeRequest request) {
  int roll=ThreadLocalRandom.current().nextInt(100);
  sleep(ThreadLocalRandom.current().nextInt(150,650));
  if(request.amount().compareTo(new BigDecimal("1000"))>=0){sleep(2000);throw new RuntimeException("Gateway timeout");}
  if(roll<15){sleep(1500);throw new RuntimeException("Gateway timeout");}
  boolean approved=request.cardNumber()==null || !request.cardNumber().endsWith("0002");
  return new GatewayReceipt("txn_"+UUID.randomUUID().toString().replace("-","").substring(0,20),approved,approved?"approved":"card_declined",Instant.now());
 }
 private static void sleep(long ms){try{Thread.sleep(ms);}catch(InterruptedException e){Thread.currentThread().interrupt();throw new RuntimeException(e);}}
 public record GatewayReceipt(String transactionId,boolean approved,String responseCode,Instant processedAtUtc){}
}
