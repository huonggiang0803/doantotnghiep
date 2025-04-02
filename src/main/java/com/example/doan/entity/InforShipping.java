package com.example.doan.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipping")
public class InforShipping extends AbstractEntity{
    @Column(name = "full_name")
    private String fullName; 

    @Column(name = "phone")
    private String phone; 

    @Column(name = "address")
    private String address;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDefault;
}
