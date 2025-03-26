package com.example.doan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bill_item")
public class BillItem extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill; 

    @ManyToOne
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant; 

    @Column(name = "quantity", nullable = false)
    private Integer quantity; 

    @Column(name = "price", nullable = false)
    private Double price; 

    @Column(name = "total_price", nullable = false)
    private Double totalPrice; 
    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        this.totalPrice = (this.quantity * this.price)+bill.getShippingFee();
    }
}
