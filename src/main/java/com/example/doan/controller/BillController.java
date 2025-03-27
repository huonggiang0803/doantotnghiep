package com.example.doan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.service.BillService;
@RestController
@RequestMapping("/api/bills")
public class BillController {
    @Autowired
    BillService billService;
    @PostMapping("/send-invoice/{billId}")
    public ResponseEntity<String> sendInvoice(@PathVariable Long billId) {
    try {
        billService.sendInvoiceEmail(billId);
        return ResponseEntity.ok("Hóa đơn đã gửi thành công!");
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Lỗi khi gửi hóa đơn: " + e.getMessage());
    }
}
}
