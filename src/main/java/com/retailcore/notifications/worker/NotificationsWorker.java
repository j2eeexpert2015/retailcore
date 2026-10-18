package com.retailcore.notifications.worker;
import com.retailcore.contracts.event.OrderConfirmed;
import com.retailcore.contracts.messaging.InProcessQueue;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.*;
@Component public class NotificationsWorker {
 private final InProcessQueue<OrderConfirmed> queue;private final ExecutorService executor=Executors.newSingleThreadExecutor();
 public NotificationsWorker(InProcessQueue<OrderConfirmed> queue){this.queue=queue;}
 @PostConstruct public void start(){executor.submit(()->{while(!Thread.currentThread().isInterrupted())try{
  var event=queue.take();Thread.sleep(100);System.out.println("Confirmation e-mail sent to "+event.customerEmail()+" for order "+event.reference());
 }catch(InterruptedException e){Thread.currentThread().interrupt();}});}
 @PreDestroy public void stop(){executor.shutdownNow();}
}
