package com.example.doan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.doan.entity.Cart;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long id);

    @Modifying
    @Transactional
    @Query(value = """
    DELETE FROM cart ca
    WHERE EXISTS (
        SELECT 1 FROM users u WHERE u.id = ca.user_id AND ca.user_id = :id
    )
""", nativeQuery = true)
    void deleteByUserId(Long id);

}
