package com.retailcore.orders.repository;
import com.retailcore.orders.domain.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StockItemRepository extends JpaRepository<StockItem, Integer> { java.util.Optional<StockItem> findByProductIdAndWarehouseCode(int productId,String warehouseCode); }
