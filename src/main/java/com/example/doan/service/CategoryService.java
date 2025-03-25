package com.example.doan.service;

import java.util.List;

import com.example.doan.entity.Category;
import com.example.doan.entity.Product;

public interface CategoryService {
    List<Product> productCategory(Long categoryId);
    List<Category> getAllCategory();
}
