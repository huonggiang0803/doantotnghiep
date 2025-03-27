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
        OrderDTO orderDTO = orderService.createOrderFromCart(request.getCartId(), request.getShippingId(), request.getPaymentMethod(), request.getShippingMethod());
        return ResponseEntity.ok(orderDTO);
    }
    @GetMapping("/getOrderHistory/{userId}")
public ResponseEntity<List<OrderDTO>> getOrderHistory(@PathVariable Long userId) {
    List<OrderDTO> orderHistory = orderService.getOrderHistoryByUser(userId);
    return ResponseEntity.ok(orderHistory);
}
@PostMapping("/checkout/{billId}")
    public ResponseEntity<?> checkout(@PathVariable Long billId) {
        try {
            Bill bill = billService.getBillById(billId);
            emailService.sendInvoiceEmail(bill);
            return ResponseEntity.ok("Hóa đơn đã được gửi email thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }

}
