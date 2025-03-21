package com.example.doan.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class CartItem extends AbstractEntity{
    @ManyToOne
    private Cart cart;
    
    @ManyToOne
    private ProductVariant productVariant;

    private Integer quantity;

    private Double subTotal;

    private Double price;

    private String img;

    public void calculateSubTotal() {
        if (productVariant != null) {
            double finalPrice = productVariant.getFinalPrice();
            this.subTotal = finalPrice * quantity;
        } else {
            this.subTotal = price * quantity; 
        }
    }
}
