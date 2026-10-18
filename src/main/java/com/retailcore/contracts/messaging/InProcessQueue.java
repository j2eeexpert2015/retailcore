package com.retailcore.contracts.messaging;
import java.util.concurrent.*;
public class InProcessQueue<T> {
 private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();
 public boolean tryEnqueue(T value) { return queue.offer(value); }
 public T take() throws InterruptedException { return queue.take(); }
}
