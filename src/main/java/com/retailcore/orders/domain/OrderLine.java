package com.retailcore.orders.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="OrderLines")
public class OrderLine {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="Id") public int id;
 @Column(name="OrderId") public int orderId;
 @Column(name="ProductId") public int productId;
 @Column(name="Quantity") public int quantity;
 @Column(name="UnitPrice", precision=18, scale=2) public BigDecimal unitPrice;
 @Column(name="LineTotal", precision=18, scale=2) public BigDecimal lineTotal;
}
