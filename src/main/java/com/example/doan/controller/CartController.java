package com.example.doan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.dto.CartDTO;
import com.example.doan.dto.CartItemDTO;
import com.example.doan.entity.UserEntity;
import com.example.doan.service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public ResponseEntity<CartDTO> getMyCart() {
    Long userId = getCurrentUserId(); 
    CartDTO cartDTO = cartService.getCartByUserId(userId);
    return ResponseEntity.ok(cartDTO);
}

@PostMapping("/add")
public ResponseEntity<?> addProductToCart(@RequestBody CartItemDTO itemDTO) {
    Long userId = getCurrentUserId(); 
    CartDTO updatedCart = cartService.addProduct(
        userId, 
        itemDTO.getProductVariantId(), 
        itemDTO.getQuantity(),
        itemDTO.getPrice(),
        itemDTO.getImageUrl()
    );
    return ResponseEntity.ok(updatedCart);
}
private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserEntity user) {
        return user.getId();
    }
    throw new RuntimeException("Người dùng chưa đăng nhập!");
}
@PutMapping("/update")
public ResponseEntity<CartDTO> updateCartItemQuantity(@RequestBody CartItemDTO itemDTO) {
    Long userId = getCurrentUserId(); 
    CartDTO updatedCart = cartService.updateQuantity(userId, itemDTO.getProductVariantId(), itemDTO.getQuantity());
    return ResponseEntity.ok(updatedCart);
}
    @DeleteMapping("/remove/{productId}")
public ResponseEntity<CartDTO> removeProductFromCart(@PathVariable Long productId) {
    Long userId = getCurrentUserId(); 
    CartDTO updatedCart = cartService.delete(userId, productId);
    return ResponseEntity.ok(updatedCart);
}

    // // Xoá toàn bộ giỏ hàng
    // @DeleteMapping("/clear/{userId}")
    // public ResponseEntity<String> clearCart(@PathVariable Long userId) {
    //     cartService.clearCart(userId);
    //     return ResponseEntity.ok("Giỏ hàng đã được xoas!");
    // }
}
