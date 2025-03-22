package com.example.doan.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.example.doan.dto.ProductDTO;
import com.example.doan.entity.Product;

public interface ProductService {
    public Long saveProduct(ProductDTO productDTO, MultipartFile imageFile);
    Product getProductById(long id);
    void deleteProductByID(long id);
    List<Product> getAllProduct(String keyword);
    Page<Product> findPaginated(int pageNo, int pageSize, String sortFileld, String sortDir);
    public void updateProduct(Long id, ProductDTO productDTO, List<MultipartFile> photos);
    List<ProductDTO> geProductDTOs( String size, String color, Double minPrice, Double maxPrice, String keyword);
    List<String> getAllBrands();
}
