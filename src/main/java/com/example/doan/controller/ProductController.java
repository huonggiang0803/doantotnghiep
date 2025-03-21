package com.example.doan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.doan.dto.ProductDTO;
import com.example.doan.entity.Product;
import com.example.doan.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(value = "keyword", required = false) String keyword) {
        List<Product> products = productService.getAllProduct(keyword);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/filter")
    public ResponseEntity<List<ProductDTO>> filterProduct(@RequestParam(required = false) String size,
    @RequestParam(required = false) String color,
    @RequestParam(required = false) Double minPrice,
    @RequestParam(required = false) Double maxPrice
    ){
        List<ProductDTO> filteredProducts = productService.geProductDTOs(size, color, minPrice, maxPrice);
        return ResponseEntity.ok(filteredProducts);
    }
    @PostMapping("save")
    public ResponseEntity<String> createProduct(
    @ModelAttribute ProductDTO productDTO,
    @RequestParam("photos") MultipartFile imageFile
) {
        Long productId = productService.saveProduct(productDTO, imageFile);
        return ResponseEntity.ok("Sản phẩm đã được lưu với ID: " + productId);
}
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id,
                                            @ModelAttribute ProductDTO productDTO,
                                            @RequestParam("photos") List<MultipartFile> photos) {
    productService.updateProduct(id, productDTO, photos);
    return ResponseEntity.ok("Sản phẩm đã được cập nhật!");
}

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        productService.deleteProductByID(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/page/{pageNo}")
    public ResponseEntity<Page<Product>> getPaginatedProducts(
            @PathVariable int pageNo,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir) {
        int pageSize = 5;
        Page<Product> page = productService.findPaginated(pageNo, pageSize, sortField, sortDir);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/brands")
    public List<String> getAllBrands() {
        return productService.getAllBrands();
    }
}
