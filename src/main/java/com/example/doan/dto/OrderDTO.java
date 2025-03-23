package com.example.doan.dto;

import java.util.List;

import com.example.doan.status.OrderEnum;
import com.example.doan.status.PaymentStatus;
import com.example.doan.status.ShipingEnum;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class OrderDTO {
    private Long id;
    private Long customerId;
    private Double totalPrice;
    private Double shippingFee;
    private ShipingEnum shippingMethod;
    private OrderEnum status;
    private PaymentStatus paymentStatus;
    private Long shippingId;
    private String setShippingAddress;
    private String paymentMethod;
    private String note;
    private List<OrderItemDTO> orderItems;
}
