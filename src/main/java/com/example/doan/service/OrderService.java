package com.example.doan.service;

import com.example.doan.dto.OrderDTO;

public interface OrderService {
    public OrderDTO createOrderFromCart(Long userId, Long shippingAddress, String paymentMethod);

}
