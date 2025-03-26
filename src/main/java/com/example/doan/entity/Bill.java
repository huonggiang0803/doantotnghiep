package com.example.doan.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@Table(name = "bill")
public class Bill extends AbstractEntity{
    
    @OneToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "shipping_id", nullable = false)
    private InforShipping shipping;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    private List<BillItem> billItems;

    private double totalAmount;
    private String paymentMethod;
    
    private String status;

}
