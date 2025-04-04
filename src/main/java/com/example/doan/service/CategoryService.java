package com.example.doan.service;

import java.util.List;

import com.example.doan.entity.Category;
import com.example.doan.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<Product> productCategory(Long categoryId, Pageable pageable);
    List<Category> getAllCategory();
    Category addCategory(Category category);
    void deleteCategory(Long id);
    Category updateCategory(Long id, Category categoryDetails);

}
