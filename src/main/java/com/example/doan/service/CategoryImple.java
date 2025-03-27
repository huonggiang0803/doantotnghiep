package com.example.doan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.doan.entity.Category;
import com.example.doan.entity.Product;
import com.example.doan.repository.CategoryRepository;

@Service
@Transactional
public class CategoryImple implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Product> productCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        return category.getProducts();
    }

    @Override
    public List<Category> getAllCategory() {
       return categoryRepository.findAll();
    }

    @Override
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

}
