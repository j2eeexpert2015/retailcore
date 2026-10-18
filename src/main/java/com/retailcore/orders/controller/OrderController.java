package com.retailcore.orders.controller;
import com.retailcore.orders.service.OrderManager;
import com.retailcore.contracts.dto.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Map;
@RestController public class OrderController {
 private final OrderManager manager;public OrderController(OrderManager manager){this.manager=manager;}
 @PostMapping("/orders") public ResponseEntity<?> create(@RequestBody CreateOrderRequest request){var outcome=manager.createOrder(request);
  return outcome.success()?ResponseEntity.created(URI.create("/orders/"+outcome.order().id())).body(outcome.order()):ResponseEntity.status(409).body(Map.of("error",outcome.error()));}
 @GetMapping("/orders/{id}") public ResponseEntity<?> details(@PathVariable int id){var result=manager.getOrderDetails(id);return result==null?ResponseEntity.notFound().build():ResponseEntity.ok(result);}
 @GetMapping("/health") public Map<String,String> health(){return Map.of("status","ok","service","retailcore");}
}
