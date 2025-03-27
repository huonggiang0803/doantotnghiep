package com.example.doan.entity;

import java.util.ArrayList;
import java.util.List;


import com.example.doan.status.OrderEnum;
import com.example.doan.status.PaymentStatus;
import com.example.doan.status.ShipingEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Orders extends AbstractEntity{
    @ManyToOne
    private UserEntity userId; 

    @Column(name = "total_price")
    private Double totalPrice;

    @ManyToOne
    @JoinColumn(name = "shipping_id") 
    private InforShipping inforShipping;

    @Column(name = "shipping_fee")
    private Double shippingFee; 

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_method")
    private ShipingEnum shippingMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentStatus")
    private PaymentStatus paymentStatus;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderEnum orderEnum;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void calculateTotalAmount() {
        Double total = 0.0;
        for (OrderItem item : items) {
            total += item.getSubTotal();
        }
        this.totalPrice = total + calculateShippingFee();
        this.shippingFee = calculateShippingFee();
    }
    public Double calculateShippingFee() {
        if (this.shippingMethod == null) return 30000.0;
        switch (this.shippingMethod) {
            case STANDARD:
                return 30000.0; 
            case EXPRESS:
                return 50000.0; 
            case SAME_DAY:
                return 70000.0; 
            default:
                return 0.0;
        }
    }
}
