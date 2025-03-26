package com.example.doan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.doan.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> search(@Param("keyword") String keyword);
    
    public Product findByProductName(String productName);
    Optional<Product> findById(Long id);

    @Query("SELECT DISTINCT p FROM Product p " +
           "JOIN p.variants v " +
           "WHERE (:size IS NULL OR v.size = :size) " +   // Nếu size không null, tìm sản phẩm có size đó
           "AND (:color IS NULL OR v.color = :color) " +  // Nếu color không null, tìm sản phẩm có màu đó
           "AND (:minPrice IS NULL OR v.price >= :minPrice) " + // Nếu minPrice không null, tìm sản phẩm có giá >= minPrice
           "AND (:maxPrice IS NULL OR v.price <= :maxPrice)")   // Nếu maxPrice không null, tìm sản phẩm có giá <= maxPrice
    List<Product> findFilteredProducts(
            @Param("size") String size,
            @Param("color") String color,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );
    Page<Product> findByProductNameContainingIgnoreCase(String productName, Pageable pageable);
}