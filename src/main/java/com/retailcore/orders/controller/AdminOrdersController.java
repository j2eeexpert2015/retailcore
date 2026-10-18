package com.retailcore.orders.controller;
import com.retailcore.orders.service.OrderManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller public class AdminOrdersController {
 private final OrderManager manager;public AdminOrdersController(OrderManager manager){this.manager=manager;}
 @GetMapping("/admin/orders") public String orders(Model model){model.addAttribute("orders",manager.recentOrders());return "admin/orders";}
}
