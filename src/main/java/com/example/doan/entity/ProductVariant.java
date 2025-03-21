package com.example.doan.entity;

import com.example.doan.status.ProductStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product_variant")
public class ProductVariant extends AbstractEntity {
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "size", nullable = false)
    private String size;

    @Column(name = "material")
    private String material;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "discount_percentage")
    private Double discountPercentage; 

    @Column(name = "promotional_price")
    private Double promotionalPrice;

    @Column(name = "stock", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer stock;

    @Column(name = "product_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @PrePersist
    @PreUpdate
    public void updatePromotionalPrice() {
        if (discountPercentage != null && discountPercentage > 0) {
            this.promotionalPrice = price - (price * discountPercentage / 100);
        } else {
            this.promotionalPrice = price;
        }
    }

    public double getFinalPrice() {
        return promotionalPrice;
    }
    
    public void setStock(Integer stock) {
       
        // if (this.product != null) {
        //     this.product.updateStatus();
        // }
        this.stock = stock;
        updateStatus();
    }
    
    public void updateStatus() {
        this.productStatus = (this.stock > 0) ? ProductStatus.AVAILABLE : ProductStatus.OUT_OF_STOCK;
    }
    
}
