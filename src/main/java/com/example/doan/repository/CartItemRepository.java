package com.example.doan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.entity.Cart;
import com.example.doan.entity.CartItem;
import com.example.doan.entity.UserEntity;

@Repository
public interface CartItemRepository  extends JpaRepository<CartItem , Long>{
    void deleteByProductVariant_Product_Id(Long productVariantId);
    List<CartItem> findByCart(Cart cart);
    List<CartItem> findByCart_User(UserEntity user);
}

