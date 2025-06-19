package com.api.libreria.controller;

import com.api.libreria.model.Order;
import com.api.libreria.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<Order> createOrderFromCart(@PathVariable Long userId) {
        Order order = orderService.createOrderFromCart(userId);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }
}
