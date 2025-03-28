package com.example.doan.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class OrderItem extends AbstractEntity {
    @ManyToOne
    private Orders order;

    @ManyToOne
    private ProductVariant product; 

    private Integer quantity; 

    private Double subTotal; 

    private Double price; 

    private String img; 

    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;
    public void calculateSubTotal() {
        this.subTotal = product.getFinalPrice() * this.quantity;
    }
}