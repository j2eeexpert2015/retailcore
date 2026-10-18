package com.retailcore.orders.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="Products")
public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="Id") public int id;
 @Column(name="Sku", length=32, unique=true) public String sku;
 @Column(name="Name", length=200) public String name;
 @Column(name="Category", length=64) public String category;
 @Column(name="UnitPrice", precision=18, scale=2) public BigDecimal unitPrice;
 @Column(name="WeightKg", precision=10, scale=3) public BigDecimal weightKg;
 @Column(name="Discontinued") public boolean discontinued;
}
