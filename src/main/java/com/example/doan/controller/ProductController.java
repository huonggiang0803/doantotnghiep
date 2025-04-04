package com.example.doan.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.example.doan.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequiredArgsConstructor
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    private final ProductRepository productRepository;

    @GetMapping("/get-all-product")
    public ResponseEntity<Map<String, Object>> getAllProducts(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "pageSize", defaultValue = "1000") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.findAll(keyword, pageable);

        Map<String, Object> listPage = new HashMap<>();
        listPage.put("totalPages", productPage.getTotalPages());
        listPage.put("totalElements", productPage.getTotalElements());
        listPage.put("numberOfElements", productPage.getNumberOfElements());
        listPage.put("products", productPage.getContent());

        return ResponseEntity.ok(listPage);
    }

//    @GetMapping("/filterProduct")
//    public ResponseEntity<Map<String, Object>> filterProduct(
//            @RequestParam(required = false) String size, // Kích thước sản phẩm
//            @RequestParam(required = false) String color,
//            @RequestParam(required = false) Double minPrice,
//            @RequestParam(required = false) Double maxPrice,
//            @RequestParam(value = "page", defaultValue = "0") int page,
//            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) { // Đổi tên size thành pageSize
//
//        List<String> sizes = (size != null && !size.isEmpty())
//                ? Arrays.asList(size.split(","))
//                : null;
//
//        List<String> colors = (color != null && !color.isEmpty())
//                ? Arrays.asList(color.toLowerCase().split(","))
//                : null;
//
//        Pageable pageable = PageRequest.of(page, pageSize); // Sử dụng pageSize thay vì size
//        Page<ProductDTO> productPage = productService.geProductDTOs(sizes, colors, minPrice, maxPrice, pageable);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("totalPages", productPage.getTotalPages());
//        response.put("totalElements", productPage.getTotalElements());
//        response.put("numberOfElements", productPage.getNumberOfElements());
//        response.put("products", productPage.getContent());
//
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/filterProduct")
    public ResponseEntity<Map<String, Object>> filterProduct(
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String color,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "8") int pageSize) {

        List<String> sizes = (size != null && !size.isEmpty())
                ? Arrays.asList(size.split(","))
                : null;

        List<String> colors = (color != null && !color.isEmpty())
                ? Arrays.asList(color.toLowerCase().split(","))
                : null;

        List<Double> priceRange = null;
        if (price != null && !price.isEmpty()) {
            priceRange = Arrays.stream(price.split(","))
                    .map(Double::parseDouble)
                    .collect(Collectors.toList());
        }

        // Sử dụng priceRange để lọc sản phẩm
        Double minPrice = priceRange != null && priceRange.size() > 0 ? priceRange.get(0) : null;
        Double maxPrice = priceRange != null && priceRange.size() > 1 ? priceRange.get(1) : null;

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<ProductDTO> productPage = productService.geProductDTOs(sizes, colors, minPrice, maxPrice, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("totalPages", productPage.getTotalPages());
        response.put("totalElements", productPage.getTotalElements());
        response.put("products", productPage.getContent());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/addProductNew")
    public ResponseEntity<String> createProduct(
    @ModelAttribute ProductDTO productDTO,
    @RequestParam("photos") List<MultipartFile> imageFile
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
    public ResponseEntity<Map<String, Object>> getAllCategory(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "8") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> products = productRepository.findByCategoryId(id, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("totalElements", products.getTotalElements()); // Tổng số sản phẩm
        response.put("totalPages", products.getTotalPages()); // Tổng số trang
        response.put("numberOfElements", products.getSize()); // Số sản phẩm trên mỗi trang
        response.put("products", products.getContent());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategory();
        return ResponseEntity.ok(categories);
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

    @PutMapping(value = "/updateProduct/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductDTO productDTO,
            @RequestParam("photos") List<MultipartFile> imageFiles) {
        try {
            productService.updateProduct(id, productDTO, imageFiles);
            return ResponseEntity.ok("Sản phẩm đã được cập nhật thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi xảy ra: " + e.getMessage());
        }
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
