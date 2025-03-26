package com.example.doan.service;

import com.example.doan.dto.CartDTO;

public interface CartService {
    CartDTO getCartByUserId(Long userId);

    CartDTO addProduct(Long userId, Long productId, Integer quantity, Double price, String img);

    CartDTO updateQuantity(Long userId, Long productId, Integer quantity);

    CartDTO delete(Long userId, Long productId);

    void xoaAll(Long userId);
    public CartDTO deleteCartItemById(Long cartItemId);
}
