package com.retailcore.orders.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="Orders")
public class Order {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="Id") public int id;
 @Column(name="Reference", length=32, unique=true) public String reference;
 @Column(name="CustomerEmail", length=200) public String customerEmail;
 @Column(name="ShippingRegion", length=8) public String shippingRegion;
 @Column(name="Status", length=16) public String status;
 @Column(name="Subtotal", precision=18, scale=2) public BigDecimal subtotal;
 @Column(name="DiscountAmount", precision=18, scale=2) public BigDecimal discountAmount;
 @Column(name="TaxAmount", precision=18, scale=2) public BigDecimal taxAmount;
 @Column(name="ShippingAmount", precision=18, scale=2) public BigDecimal shippingAmount;
 @Column(name="Total", precision=18, scale=2) public BigDecimal total;
 @Column(name="PaymentId", length=64) public String paymentId;
 @Column(name="CreatedAtUtc") public Instant createdAtUtc;
 @Column(name="ConfirmedAtUtc") public Instant confirmedAtUtc;
 @Column(name="Notified") public boolean notified;
}
