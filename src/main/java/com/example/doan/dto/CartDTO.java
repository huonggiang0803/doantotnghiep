package com.example.doan.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.doan.entity.CartItem;
import com.example.doan.entity.ProductVariant;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class CartDTO {
    private Long id;
    private Long userId;
    private Double totalPrice;
    private List<CartItemDTO> items = new ArrayList<>();

    public List<CartItem> toCartItemList() {
        return items.stream().map(cartItemDTO -> {
            CartItem cartItem = new CartItem();
            cartItem.setId(cartItemDTO.getId());
            cartItem.setQuantity(cartItemDTO.getQuantity());

            // Tạo ProductVariant và gán dữ liệu
            ProductVariant productVariant = new ProductVariant();
            productVariant.setId(cartItemDTO.getProductVariantId());
            productVariant.setPrice(cartItemDTO.getPrice());

            cartItem.setProductVariant(productVariant);
            return cartItem;
        }).collect(Collectors.toList());
    }

}
