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
            "WHERE (:sizes IS NULL OR v.size IN :sizes) " +
            "AND (:colors IS NULL OR v.color IN :colors) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice)")
    Page<Product> findProductsByFilters(
            @Param("sizes") List<String> sizes,
            @Param("colors") List<String> colors,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);
    Page<Product> findByProductNameContainingIgnoreCaseAndIsDeleted(String productName, byte isDeleted, Pageable pageable);
}