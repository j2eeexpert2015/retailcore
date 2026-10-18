package com.retailcore.orders.service;
import com.retailcore.contracts.dto.*;
import com.retailcore.orders.domain.*;
import com.retailcore.orders.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import com.retailcore.payments.service.PaymentsClient;
import java.math.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
/** Deliberately centralizes business workflows like the original OrderManager. */
@Service public class OrderManager {
 private final OrderRepository orders;private final OrderLineRepository lines;private final ProductRepository products;private final StockService stock;
 private final PaymentsClient payments;private final String currency;
 public OrderManager(OrderRepository orders,OrderLineRepository lines,ProductRepository products,StockService stock,
 PaymentsClient payments,@Value("${payments.currency:USD}") String currency){
 this.orders=orders;this.lines=lines;this.products=products;this.stock=stock;this.payments=payments;this.currency=currency;}
 public record Outcome(boolean success,String error,OrderDto order){}
 public Outcome createOrder(CreateOrderRequest req){
  String email=req.customerEmail()==null?"":req.customerEmail().trim().toLowerCase(Locale.ROOT);
  String region=req.shippingRegion()==null?"":req.shippingRegion().trim().toUpperCase(Locale.ROOT);
  Order order=new Order();order.reference=generateOrderReference();order.customerEmail=email;order.shippingRegion=region;order.status=OrderStatus.Pending;order.createdAtUtc=Instant.now();
  List<OrderLine> pendingLines=new ArrayList<>();List<CreateOrderLine> reserved=new ArrayList<>();
  BigDecimal subtotal=BigDecimal.ZERO,weight=BigDecimal.ZERO;int units=0;
  for(CreateOrderLine requested:req.lines()){
   Product p=getProduct(requested.productId());
   BigDecimal price=requested.unitPrice()!=null?requested.unitPrice():p!=null?p.unitPrice:BigDecimal.ZERO;
   BigDecimal lineTotal=roundCurrency(price.multiply(BigDecimal.valueOf(requested.quantity())));
   if(p!=null){
    if(!stock.reserve(requested.productId(),requested.quantity())){
     reserved.forEach(x->stock.release(x.productId(),x.quantity()));
     return new Outcome(false,"Product "+requested.productId()+" is out of stock.",null);
    }
    reserved.add(requested);weight=weight.add(p.weightKg.multiply(BigDecimal.valueOf(requested.quantity())));
   }
   OrderLine line=new OrderLine();line.productId=requested.productId();line.quantity=requested.quantity();line.unitPrice=price;line.lineTotal=lineTotal;pendingLines.add(line);
   subtotal=subtotal.add(lineTotal);units+=requested.quantity();
  }
  subtotal=roundCurrency(subtotal);
  BigDecimal discount=roundCurrency(subtotal.multiply(volumeDiscount(units).add(seasonalDiscount(order.createdAtUtc))).divide(BigDecimal.valueOf(100)));
  BigDecimal taxable=subtotal.subtract(discount);
  BigDecimal tax=calculateTax(region,taxable);
  BigDecimal shipping=calculateShipping(region,weight,taxable);
  order.subtotal=subtotal;order.discountAmount=discount;order.taxAmount=tax;order.shippingAmount=shipping;
  order.total=req.total()!=null?req.total():roundCurrency(taxable.add(tax).add(shipping)); // client total intentionally trusted
  order=orders.save(order);
  for(OrderLine line:pendingLines){line.orderId=order.id;lines.save(line);}
  PaymentResult result=chargePayment(order,req.cardNumber());
  if("Approved".equals(result.status())){
   order.status=OrderStatus.Confirmed;order.confirmedAtUtc=Instant.now();order.paymentId=result.paymentId();orders.save(order);
   MemoryCatalogCache.set("order-pricing:"+order.reference, order); // intentionally no eviction
  }else if("Declined".equals(result.status())){
   order.status=OrderStatus.Declined;order.paymentId=result.paymentId();orders.save(order);reserved.forEach(x->stock.release(x.productId(),x.quantity()));
  }
  return new Outcome(true,null,map(order));
 }
 private PaymentResult chargePayment(Order order,String cardNumber){
  try {var request=new PaymentChargeRequest(order.reference,order.customerEmail,order.total,currency,cardNumber==null||cardNumber.isBlank()?"4242424242424242":cardNumber.trim());
   PaymentResult result=payments.charge(request);
   return result!=null?result:new PaymentResult(null,"Pending",null,Instant.now());
  }catch(Exception ignored){return new PaymentResult(null,"Pending",null,Instant.now());}
 }
 private Product getProduct(int id){Object cached=MemoryCatalogCache.get("product:"+id);if(cached instanceof Product p)return p;
  var p=products.findById(id).orElse(null);if(p!=null)MemoryCatalogCache.set("product:"+id,p);return p;}
 public OrderDetailsDto getOrderDetails(int id){var o=orders.findById(id).orElse(null);if(o==null)return null;
  List<OrderLineDto> details=new ArrayList<>();
  for(OrderLine l:lines.findByOrderIdOrderByIdAsc(id)){
   // Deliberate N+1 lookups, matching the starter's performance exercise.
   var product=products.findById(l.productId).orElse(null);
   details.add(new OrderLineDto(l.productId,product==null?null:product.sku,product==null?null:product.name,product==null?null:product.category,l.quantity,l.unitPrice,l.lineTotal));
  }
  Instant delivery=o.createdAtUtc==null?null:o.createdAtUtc.plus(Duration.ofDays(isDomestic(o.shippingRegion)?5:12));
  return new OrderDetailsDto(o.id,o.reference,o.status,o.customerEmail,o.subtotal,o.discountAmount,o.taxAmount,o.shippingAmount,o.total,o.createdAtUtc,delivery,details);
 }
 public List<OrderDto> recentOrders(){return orders.findTop100ByOrderByCreatedAtUtcDesc().stream().map(this::map).toList();}
 private OrderDto map(Order o){return new OrderDto(o.id,o.reference,o.status,o.customerEmail,o.subtotal,o.discountAmount,o.taxAmount,o.shippingAmount,o.total,o.createdAtUtc,null);}
 public static BigDecimal roundCurrency(BigDecimal v){return v.setScale(2,RoundingMode.HALF_UP);}
 public static String generateOrderReference(){return "RC-"+java.time.format.DateTimeFormatter.BASIC_ISO_DATE.format(LocalDate.now(ZoneOffset.UTC))+"-"+ThreadLocalRandom.current().nextInt(1000,10000);}
 private static BigDecimal volumeDiscount(int units){return BigDecimal.valueOf(units>=50?8:units>=25?5:units>=10?2.5:0);}
 private static BigDecimal seasonalDiscount(Instant at){LocalDate d=at.atZone(ZoneOffset.UTC).toLocalDate();return BigDecimal.valueOf(switch(d.getMonthValue()){
 case 1->3;case 7->2;case 11->d.getDayOfMonth()>=20?5:0;case 12->d.getDayOfMonth()<=21?4:0;default->0;});}
 private static final Map<String,String> TAX_RATES=Map.ofEntries(
 Map.entry("AL","4"),
 Map.entry("AK","0"),
 Map.entry("AZ","5.6"),
 Map.entry("AR","6.5"),
 Map.entry("CA","7.25"),
 Map.entry("CO","2.9"),
 Map.entry("CT","6.35"),
 Map.entry("DE","0"),
 Map.entry("FL","6"),
 Map.entry("GA","4"),
 Map.entry("HI","4"),
 Map.entry("ID","6"),
 Map.entry("IL","6.25"),
 Map.entry("IN","7"),
 Map.entry("IA","6"),
 Map.entry("KS","6.5"),
 Map.entry("KY","6"),
 Map.entry("LA","4.45"),
 Map.entry("ME","5.5"),
 Map.entry("MD","6"),
 Map.entry("MA","6.25"),
 Map.entry("MI","6"),
 Map.entry("MN","6.875"),
 Map.entry("MS","7"),
 Map.entry("MO","4.225"),
 Map.entry("MT","0"),
 Map.entry("NE","5.5"),
 Map.entry("NV","6.85"),
 Map.entry("NH","0"),
 Map.entry("NJ","6.625"),
 Map.entry("NM","4.875"),
 Map.entry("NY","4"),
 Map.entry("NC","4.75"),
 Map.entry("ND","5"),
 Map.entry("OH","5.75"),
 Map.entry("OK","4.5"),
 Map.entry("OR","0"),
 Map.entry("PA","6"),
 Map.entry("RI","7"),
 Map.entry("SC","6"),
 Map.entry("SD","4.2"),
 Map.entry("TN","7"),
 Map.entry("TX","6.25"),
 Map.entry("UT","6.1"),
 Map.entry("VT","6"),
 Map.entry("VA","5.3"),
 Map.entry("WA","6.5"),
 Map.entry("WV","6"),
 Map.entry("WI","5"),
 Map.entry("WY","4"),
 Map.entry("DC","6"),
 Map.entry("UK","20"),
 Map.entry("DE-EU","19"),
 Map.entry("FR","20"),
 Map.entry("ES","21"),
 Map.entry("IT","22"),
 Map.entry("NL","21"),
 Map.entry("SE","25"),
 Map.entry("PL","23"),
 Map.entry("IE","23"),
 Map.entry("PT","23"),
 Map.entry("AT","20"),
 Map.entry("BE","21"),
 Map.entry("DK","25"),
 Map.entry("FI","24"));
 private static BigDecimal calculateTax(String region,BigDecimal amount){if(amount.signum()<=0)return BigDecimal.ZERO.setScale(2);
  return roundCurrency(amount.multiply(new BigDecimal(TAX_RATES.getOrDefault(region,"5"))).divide(BigDecimal.valueOf(100)));}
 private static boolean isDomestic(String r){return !Set.of("UK","DE-EU","FR","ES","IT","NL","SE","PL","IE","PT","AT","BE","DK","FI").contains(r);}
 private static BigDecimal calculateShipping(String region,BigDecimal weight,BigDecimal taxable){if(taxable.compareTo(new BigDecimal("150"))>=0)return BigDecimal.ZERO.setScale(2);
  boolean domestic=isDomestic(region);BigDecimal base=new BigDecimal(domestic?"6.95":"19.95");BigDecimal perKg=new BigDecimal(domestic?"1.10":"4.25");
  BigDecimal billable=weight.max(new BigDecimal("0.1"));BigDecimal surcharge=Set.of("AK","HI").contains(region)?new BigDecimal("12.50"):BigDecimal.ZERO;
  return roundCurrency(base.add(billable.multiply(perKg)).add(surcharge));}
}
