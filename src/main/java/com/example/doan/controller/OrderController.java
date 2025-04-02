package com.example.doan.controller;

import com.example.doan.dto.OrderDTO;
import com.example.doan.entity.Bill;
import com.example.doan.entity.CreateOrderRequest;
import com.example.doan.entity.Orders;
import com.example.doan.service.BillService;
import com.example.doan.service.EmailService;
import com.example.doan.service.OrderService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private BillService billService;

    @PostMapping("/createOrder")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        if (request.getShippingId() == null) {
            throw new IllegalArgumentException("Shipping ID must not be null");
        }
        if (request.getCartId() == null) {
            throw new IllegalArgumentException("Cart ID must not be null");
        }
        System.out.println("Shipping ID received: " + request.getShippingId()); 
        OrderDTO orderDTO = orderService.createOrderFromCart(
                request.getCartId(),
                request.getShippingId(),
                request.getPaymentMethod(),
                request.getShippingMethod()
        );
        return ResponseEntity.ok(orderDTO);
    }
    @GetMapping("/getOrderHistory/{userId}")
public ResponseEntity<List<OrderDTO>> getOrderHistory(@PathVariable Long userId) {
    List<OrderDTO> orderHistory = orderService.getOrderHistoryByUser(userId);
    return ResponseEntity.ok(orderHistory);
}
@PostMapping("/createBill/{orderId}")
public ResponseEntity<?> generateBill(@PathVariable Long orderId) {
    try {
        Bill bill = billService.createBill(orderId);
        return ResponseEntity.ok("Hóa đơn đã được tạo thành công!");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
    }
}

}
