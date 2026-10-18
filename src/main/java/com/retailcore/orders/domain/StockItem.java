package com.retailcore.orders.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="StockItems")
public class StockItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="Id") public int id;
 @Column(name="ProductId") public int productId;
 @Column(name="WarehouseCode", length=16) public String warehouseCode;
 @Column(name="QuantityAvailable") public int quantityAvailable;
 @Column(name="UpdatedAtUtc") public Instant updatedAtUtc;
}
