package com.retailcore.orders.service;
import java.time.Instant;
import java.util.*;
/** Unbounded process-wide cache intentionally retained from the original. */
public final class MemoryCatalogCache {
 private static final Map<String,Entry> ENTRIES=new HashMap<>();
 private MemoryCatalogCache(){}
 public static Object get(String key){Entry e=ENTRIES.get(key);if(e==null)return null;e.hits++;return e.value;}
 public static void set(String key,Object value){ENTRIES.put(key,new Entry(value,Instant.now()));}
 public static int count(){return ENTRIES.size();}
 private static class Entry {Object value;Instant at;long hits;Entry(Object v,Instant t){value=v;at=t;}}
}
