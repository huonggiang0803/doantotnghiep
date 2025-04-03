package com.example.doan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.doan.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId")
    List<Cart> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
