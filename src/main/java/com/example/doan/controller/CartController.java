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

import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/addProductToCart")
    public ResponseEntity<?> addProductToCart(@RequestBody CartItemDTO itemDTO) {
        Long userId = getCurrentUserId();
        CartDTO updatedCart = cartService.addProduct(
                userId,
                itemDTO.getProductVariantId(),
                itemDTO.getQuantity(),
                itemDTO.getPrice(),
                itemDTO.getImageUrl()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Thêm vào giỏ hàng thành công!");
        response.put("cart", updatedCart);

        return ResponseEntity.ok(response);
    }
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserEntity user) {
            return user.getId();
        }
        throw new RuntimeException("Người dùng chưa đăng nhập!");
    }
// @PutMapping("/updateCartItemQuantity")
//     public ResponseEntity<CartDTO> updateCartItemQuantity(@RequestBody CartItemDTO itemDTO) {
//         Long userId = getCurrentUserId(); 
//         CartDTO updatedCart = cartService.updateQuantity(userId, itemDTO.getProductVariantId(), itemDTO.getQuantity());
//     return ResponseEntity.ok(updatedCart);
// }
    @PutMapping("/updateCartItemQuantity")
    public ResponseEntity<CartDTO> updateCartItemQuantity(@RequestBody CartItemDTO itemDTO) {
        Long userId = getCurrentUserId(); 
        CartDTO updatedCart = cartService.updateQuantity(userId, itemDTO.getId(), itemDTO.getQuantity());
    return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/removeProductFromCart/{productId}")
    public ResponseEntity<CartDTO> removeProductFromCart(@PathVariable Long productId) {
        Long userId = getCurrentUserId(); 
        CartDTO updatedCart = cartService.delete(userId, productId);
    return ResponseEntity.ok(updatedCart);
    }
    @DeleteMapping("/removeCartItem/{cartItemId}")
    public ResponseEntity<CartDTO> removeCartItem(@PathVariable Long cartItemId) {
        CartDTO updatedCart = cartService.deleteCartItemById(cartItemId);
    return ResponseEntity.ok(updatedCart);
}

    // // Xoá toàn bộ giỏ hàng
    // @DeleteMapping("/clear/{userId}")
    // public ResponseEntity<String> clearCart(@PathVariable Long userId) {
    //     cartService.clearCart(userId);
    //     return ResponseEntity.ok("Giỏ hàng đã được xoas!");
    // }
}
