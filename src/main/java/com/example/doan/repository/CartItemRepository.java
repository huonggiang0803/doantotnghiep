package com.example.doan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.doan.entity.Cart;
import com.example.doan.entity.CartItem;
import com.example.doan.entity.UserEntity;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartItemRepository  extends JpaRepository<CartItem , Long>{
    void deleteByProductVariant_Product_Id(Long productVariantId);
    List<CartItem> findByCart(Cart cart);
    List<CartItem> findByCart_User(UserEntity user);

    @Modifying
    @Transactional
    @Query(value = """
    DELETE FROM cart_item
    USING cart ca, users u
    WHERE cart_item.cart_id = ca.id
    AND ca.user_id = u.id
    AND ca.user_id = :id
""", nativeQuery = true)
    void deleteByUserId(Long id);
}

