package com.example.doan.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    private Long cartId;
    private Long shippingId;
    private String paymentMethod;
    private String shippingMethod;
}
