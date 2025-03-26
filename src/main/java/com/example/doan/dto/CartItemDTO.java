package com.example.doan.dto;

import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class CartItemDTO {
    private Long id;
    private Long productVariantId;
    private String nameProduct;
    private Integer quantity;
    private Double subTotal;
    private Double price;
    private String size;
    private String material;
    private String color;
    private String imageUrl;
}
