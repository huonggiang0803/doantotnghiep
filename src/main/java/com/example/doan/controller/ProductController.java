package com.example.doan.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.doan.dto.ProductDTO;
import com.example.doan.entity.Category;
import com.example.doan.entity.Product;
import com.example.doan.entity.ProductImage;
import com.example.doan.service.CategoryService;
import com.example.doan.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/get-all-product")
    public ResponseEntity<Map<String, Object>> getAllProducts(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "1000") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.findAll(keyword, pageable);

        Map<String, Object> listPage = new HashMap<>();
        listPage.put("totalPages", productPage.getTotalPages());
        listPage.put("totalElements", productPage.getTotalElements());
        listPage.put("numberOfElements", productPage.getNumberOfElements());
        listPage.put("products", productPage.getContent());

        return ResponseEntity.ok(listPage);
    }

    @GetMapping("/filterProduct")
    public ResponseEntity<List<ProductDTO>> filterProduct(@RequestParam(required = false) String size,
    @RequestParam(required = false) String color,
    @RequestParam(required = false) Double minPrice,
    @RequestParam(required = false) Double maxPrice
    ){
        List<ProductDTO> filteredProducts = productService.geProductDTOs(size, color, minPrice, maxPrice);
        return ResponseEntity.ok(filteredProducts);
    }

    @PostMapping("/addProductNew")
    public ResponseEntity<String> createProduct(
    @ModelAttribute ProductDTO productDTO,
    @RequestParam("photos") MultipartFile imageFile
    ) {
        Long productId = productService.saveProduct(productDTO, imageFile);
        return ResponseEntity.ok("Sản phẩm đã được lưu với ID: " + productId);
    }
    @GetMapping("/getProductById/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    @GetMapping("/getAllCategory/{id}")
    public ResponseEntity<List<Product>> getAllCategory(@PathVariable Long id) {
        List<Product> products = categoryService.productCategory(id);
        return ResponseEntity.ok(products);
    }
    @PostMapping("/addCategory")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        Category newCategory = categoryService.addCategory(category);
        return ResponseEntity.ok(newCategory);
    }
    @DeleteMapping("deleteCategory/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    return ResponseEntity.ok("Category deleted successfully");
    }
    @PutMapping("updateCategory/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
    return ResponseEntity.ok(updatedCategory);
}
    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id,
                                            @ModelAttribute ProductDTO productDTO,
                                            @RequestParam("photos") List<MultipartFile> photos) {
    productService.updateProduct(id, productDTO, photos);
    return ResponseEntity.ok("Sản phẩm đã được cập nhật!");
}

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        productService.deleteProductByID(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/images")
    public ResponseEntity<List<String>> getProductImages(@PathVariable long id) {
    List<ProductImage> images = productService.getProductImages(id);
    List<String> imageUrls = images.stream()
                                   .map(ProductImage::getFileName)
                                   .toList();
    return ResponseEntity.ok(imageUrls);
    }
    @PutMapping("/{id}/add-images")
    public ResponseEntity<String> addImagesToProduct(
            @PathVariable Long id,
            @RequestParam("photos") List<MultipartFile> imageFiles) {

        productService.addImagesToProduct(id, imageFiles);
        return ResponseEntity.ok("Ảnh đã được thêm vào sản phẩm id: " + id);
    }
}
