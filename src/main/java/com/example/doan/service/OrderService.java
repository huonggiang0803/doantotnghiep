package com.example.doan.service;

import java.util.List;

import com.example.doan.dto.OrderDTO;
import com.example.doan.entity.Orders;
import com.example.doan.entity.UserEntity;

public interface OrderService {
    public OrderDTO createOrderFromCart(Long userId, Long shippingAddress, String paymentMethod, String shippingMethod);
    List<OrderDTO> getOrderHistoryByUser(Long userId);
     Orders updatePaymentStatus(Long orderId);
    UserEntity getUserById(Long userId);
    Orders getOrderById(Long orderId);
    void saveOrder(Orders order);
}
