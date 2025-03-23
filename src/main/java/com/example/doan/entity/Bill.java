package com.example.doan.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
public class Bill extends AbstractEntity{
    
    @ManyToOne
    @JoinColumn(name = "orderID", nullable = false)
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private UserEntity user;

    private double totalAmount; 
    private String paymentMethod; 

}
