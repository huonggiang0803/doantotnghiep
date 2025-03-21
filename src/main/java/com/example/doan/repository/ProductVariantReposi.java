package com.example.doan.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.entity.ProductVariant;

@Repository
public interface ProductVariantReposi extends JpaRepository<ProductVariant , Long>{
    List<ProductVariant> findByProductId(Long productId);
}
