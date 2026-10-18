package com.retailcore.payments.service;
import com.retailcore.contracts.dto.*;
import org.springframework.stereotype.Service;
import java.time.Instant;
@Service public class PaymentsClient {
 private final FakePaymentGateway gateway;
 public PaymentsClient(FakePaymentGateway gateway){this.gateway=gateway;}
 public PaymentResult charge(PaymentChargeRequest request){
  PaymentResult result=new PaymentResult(null,"Pending",null,Instant.now());
  try{
   var receipt=gateway.charge(request);
   result=new PaymentResult(receipt.transactionId(),receipt.approved()?"Approved":"Declined",receipt.responseCode(),receipt.processedAtUtc());
  }catch(Exception ignored){ /* intentionally retains original silent exception defect */ }
  return result;
 }
}
