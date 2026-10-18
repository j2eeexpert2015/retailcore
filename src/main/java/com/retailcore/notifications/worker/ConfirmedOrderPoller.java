package com.retailcore.notifications.worker;
import com.retailcore.contracts.event.OrderConfirmed;
import com.retailcore.contracts.messaging.InProcessQueue;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
@Component public class ConfirmedOrderPoller{
 private final JdbcTemplate db;private final InProcessQueue<OrderConfirmed> queue;
 public ConfirmedOrderPoller(JdbcTemplate db,InProcessQueue<OrderConfirmed> queue){this.db=db;this.queue=queue;}
 @Scheduled(fixedDelay=5000) public void poll(){
  List<OrderConfirmed> pending=db.query("SELECT \"Id\",\"Reference\",\"CustomerEmail\",\"Total\",\"ConfirmedAtUtc\" FROM \"Orders\" WHERE \"Status\"='Confirmed' AND \"Notified\"=false ORDER BY \"Id\" LIMIT 50",(rs,n)->new OrderConfirmed(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getBigDecimal(4),rs.getTimestamp(5)==null?java.time.Instant.now():rs.getTimestamp(5).toInstant()));
  for(var item:pending)queue.tryEnqueue(item);
  for(var item:pending)db.update("UPDATE \"Orders\" SET \"Notified\"=true WHERE \"Id\"=?",item.orderId());
 }
}
