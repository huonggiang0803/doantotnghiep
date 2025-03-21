package com.example.doan.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Entity
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private Double totalPrice;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> items = new ArrayList<>(); 
    public void calculateTotalPrice() {
        Double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubTotal(); 
        }

        this.totalPrice = total; 
    }
    
}
