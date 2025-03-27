package com.example.doan.service;

import java.util.List;

import com.example.doan.entity.Bill;
import com.example.doan.entity.OrderItem;
import com.example.doan.entity.Orders;
import com.example.doan.entity.UserEntity;

public interface BillService {
    Bill createBill(UserEntity userEntity, Orders order, List<OrderItem> cartItems, String paymentMethod);
    byte[] generateInvoicePdf(Long billId);
    void sendInvoiceEmail(Long billId);
    public void updatePaymentStatus(Long billId, boolean isPaid);
}
