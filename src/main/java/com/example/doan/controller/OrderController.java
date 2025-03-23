package com.example.doan.controller;

import com.example.doan.dto.OrderDTO;
import com.example.doan.entity.CreateOrderRequest;
import com.example.doan.service.OrderService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        OrderDTO orderDTO = orderService.createOrderFromCart(request.getCartId(), request.getShippingId(), request.getPaymentMethod(), request.getShippingMethod());
        return ResponseEntity.ok(orderDTO);
    }
    @GetMapping("/history/{userId}")
public ResponseEntity<List<OrderDTO>> getOrderHistory(@PathVariable Long userId) {
    List<OrderDTO> orderHistory = orderService.getOrderHistoryByUser(userId);
    return ResponseEntity.ok(orderHistory);
}

}
