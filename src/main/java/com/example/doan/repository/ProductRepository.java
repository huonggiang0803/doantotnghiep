package com.example.doan.repository;

import java.util.List;
import java.util.Optional;

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
            "WHERE (:size IS NULL OR v.size = :size) " +
            "AND (:color IS NULL OR v.color = :color) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
            "AND (LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')))")  // Tìm theo từ khóa
    List<Product> findFilteredProducts(
            @Param("size") String size,
            @Param("color") String color,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("keyword") String keyword);  // Thêm tham số keyword


    @Query(value = "SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL", nativeQuery = true)
    List<String> getAllDistinctBrands();
}