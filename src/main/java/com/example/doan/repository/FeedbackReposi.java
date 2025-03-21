package com.example.doan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.doan.entity.FeedBack;
public interface FeedbackReposi extends JpaRepository<FeedBack, Long>{
    @Query("SELECT COALESCE(AVG(f.rating), 0) FROM FeedBack f WHERE f.product.id = :productId")
    Double averageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(f) FROM FeedBack f WHERE f.product.id = :productId")
    Integer countByProductId(@Param("productId") Long productId);

    @Query("SELECT f FROM FeedBack f WHERE f.product.id = :productId")
    List<FeedBack> findByProductId(@Param("productId") Long productId);
}
