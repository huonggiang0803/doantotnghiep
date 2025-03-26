package com.example.doan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.doan.dto.CartDTO;
import com.example.doan.dto.CartItemDTO;
import com.example.doan.entity.Cart;
import com.example.doan.entity.CartItem;
import com.example.doan.entity.ProductVariant;
import com.example.doan.entity.UserEntity;
import com.example.doan.repository.CartItemRepository;
import com.example.doan.repository.CartRepository;
import com.example.doan.repository.ProductVariantReposi;
import com.example.doan.repository.UserRepository;

@Service
public class CartImplement implements CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductVariantReposi productVariantReposi;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public CartDTO addProduct(Long userId, Long productVariantId, Integer quantity, Double price, String img) {
        Optional<UserEntity> userOtp = userRepository.findById(userId);
        Optional<ProductVariant> prOptional = productVariantReposi.findById(productVariantId);

        if (userOtp.isEmpty()) {
            throw new RuntimeException("User không tồn tại với userId: " + userId);
        }
        if (prOptional.isEmpty()) {
            throw new RuntimeException("ProductVariant không tồn tại với productVariantId: " + productVariantId);
        }

        UserEntity user = userOtp.get();
        ProductVariant productVariant = prOptional.get();

       if (productVariant == null) {
        throw new RuntimeException("ProductVariant không hợp lệ!");
    }
    if (price == null) {
        price = productVariant.getPromotionalPrice();
    } 
    List<Cart> carts = cartRepository.findByUserId(userId); 
    if (carts.isEmpty()) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setTotalPrice(0.0);
        newCart.setItems(new ArrayList<>());
        newCart = cartRepository.save(newCart); // Lưu vào database
        carts.add(newCart);//s
    }
    else if (carts.size() > 1) {
        throw new RuntimeException("Lỗi dữ liệu: Một user có nhiều giỏ hàng!");
    }
    Cart cart = carts.get(0);

        Optional<CartItem> eOptional = cart.getItems().stream()
                .filter(item -> item.getProductVariant() != null && item.getProductVariant().getId().equals(productVariantId))
                .findFirst();

        if (img == null && productVariant.getProduct().getImages() != null
                && !productVariant.getProduct().getImages().isEmpty()) {
            img = productVariant.getProduct().getImages().get(0).getFileName();
        }

        if (eOptional.isPresent()) {
            CartItem ex = eOptional.get();
            ex.setQuantity(ex.getQuantity() + quantity);
            ex.calculateSubTotal();
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(productVariant)
                    .quantity(quantity)
                    .price(price)
                    .img(img)
                    .build();
            newItem.calculateSubTotal();
            cartItemRepository.save(newItem);   //ư
            cart.getItems().add(newItem);
        }

        cart.calculateTotalPrice();
        cartRepository.save(cart);
        cartItemRepository.saveAll(cart.getItems());
        return save(cart);
    }

    public static CartDTO save(Cart cart) {
        
        List<CartItemDTO> itemDTOs = new ArrayList<>();
        if (cart.getItems() != null) { 
            for (CartItem item : cart.getItems()) {
                itemDTOs.add(saveItem(item));
            }
        }
    
        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .totalPrice(cart.getTotalPrice())
                .items(itemDTOs)
                .build();
    }

    public static CartItemDTO saveItem(CartItem item) {
        if (item.getProductVariant() == null || item.getProductVariant().getProduct() == null) {
            throw new RuntimeException("Dữ liệu sản phẩm trong giỏ hàng bị lỗi!");
        }
        String image = null;
        if (item.getProductVariant() != null && 
        item.getProductVariant().getProduct() != null && 
        item.getProductVariant().getProduct().getImages() != null && 
        !item.getProductVariant().getProduct().getImages().isEmpty()) {
        image = item.getProductVariant().getProduct().getImages().get(0).getFileName();
    }
    
        return CartItemDTO.builder()
            .id(item.getId()) 
            .productVariantId(item.getProductVariant().getProduct().getId()) 
            .nameProduct(item.getProductVariant().getProduct().getProductName()) 
            .price(item.getPrice()) 
            .quantity(item.getQuantity())
            .subTotal(item.getSubTotal())
            .imageUrl(image)
            .size(item.getProductVariant().getSize())  
            .color(item.getProductVariant().getColor()) 
            .material(item.getProductVariant().getMaterial()) 
            .build();
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("Không tìm thấy giỏ hàng của người dùng!")
        );
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>()); 
        }
    
        return save(cart);
    }

    @Override
    public CartDTO delete(Long userId, Long productId) {
        Cart cart = cartRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("Không tìm thấy giỏ hàng!")
        );
        Optional<CartItem> productInCart = cart.getItems().stream()
        .filter(item -> item.getProductVariant() != null && item.getProductVariant().getId().equals(productId))
        .findFirst();    
        if (productInCart.isEmpty()) {
            throw new RuntimeException("Sản phẩm với ID " + productId + " không có trong giỏ hàng!");
        }
        CartItem itemToRemove = productInCart.get();
        cart.getItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);

        cart.calculateTotalPrice();

        cartRepository.save(cart);  

        return save(cart);
    }
    @Override
    public CartDTO deleteCartItemById(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng với ID: " + cartItemId));

        Cart cart = cartItem.getCart();
        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return save(cart);
    }
    
    @Override
    public void xoaAll(Long userId) {
        Cart cart = cartRepository.findById(userId).orElseThrow(() -> 
            new RuntimeException("Không tìm thấy giỏ hàng!")
        );
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }

        // @Override
        // public CartDTO updateQuantity(Long userId, Long productId, Integer quantity) {
        //     Cart cart = cartRepository.findById(userId).orElseThrow(() -> 
        //         new RuntimeException("Không tìm thấy giỏ hàng!")
        //     );
        //     Optional<CartItem> productInCart = cart.getItems().stream()
        //         .filter(item -> item.getProductVariant() != null 
        //             && item.getProductVariant().getId() != null
        //             && item.getProductVariant().getId().equals(productId))
        //         .findFirst();

        //     if (productInCart.isEmpty()) {
        //         throw new RuntimeException("Sản phẩm với ID " + productId + " không có trong giỏ hàng!");
        //     }
        //     CartItem itemToUpdate = productInCart.get();
        //     if (quantity <= 0) {
        //         cart.getItems().remove(itemToUpdate);
        //         cartItemRepository.delete(itemToUpdate);
        //     } else {
        //         itemToUpdate.setQuantity(quantity);
        //         itemToUpdate.calculateSubTotal();
        //     }
        //     cart.calculateTotalPrice();
        //     if (cart.getItems().isEmpty()) {
        //         cart.setTotalPrice(0.0);
        //     }
        //     cartRepository.save(cart);
        //     return save(cart);
        // }
@Override
    public CartDTO updateQuantity(Long userId, Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng!"));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItem.calculateSubTotal();
            cartItemRepository.save(cartItem);
        }

        Cart cart = cartItem.getCart();
        cart.calculateTotalPrice();
        cartRepository.save(cart);

    return save(cart);
}

       
    
}
