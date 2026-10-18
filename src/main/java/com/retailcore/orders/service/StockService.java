package com.retailcore.orders.service;
import com.retailcore.orders.repository.StockItemRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
@Service public class StockService {
 private final StockItemRepository stocks;
 public StockService(StockItemRepository stocks){this.stocks=stocks;}
 public boolean reserve(int productId,int quantity){
  var opt=stocks.findByProductIdAndWarehouseCode(productId,"MAIN");
  if(opt.isEmpty()||opt.get().quantityAvailable<quantity)return false;
  var stock=opt.get(); stock.quantityAvailable-=quantity;stock.updatedAtUtc=Instant.now();stocks.save(stock);return true;
 }
 public void release(int productId,int quantity){stocks.findByProductIdAndWarehouseCode(productId,"MAIN").ifPresent(s->{s.quantityAvailable+=quantity;s.updatedAtUtc=Instant.now();stocks.save(s);});}
 public int getAvailable(int productId){return stocks.findByProductIdAndWarehouseCode(productId,"MAIN").map(s->s.quantityAvailable).orElse(0);}
}
