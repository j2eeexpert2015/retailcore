package com.retailcore.notifications.worker;
import com.retailcore.contracts.event.OrderConfirmed;
import com.retailcore.contracts.messaging.InProcessQueue;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
@Configuration @EnableScheduling public class QueueConfig { @Bean public InProcessQueue<OrderConfirmed> queue(){return new InProcessQueue<>();}}
