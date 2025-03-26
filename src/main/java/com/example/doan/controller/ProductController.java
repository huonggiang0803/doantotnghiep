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

import com.example.doan.dto.PageDTO;
import com.example.doan.dto.ProductDTO;
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
    @GetMapping("/{id}/sp")
    public ResponseEntity<List<Product>> getAllCategory(@PathVariable Long id) {
        List<Product> products = categoryService.productCategory(id);
        return ResponseEntity.ok(products);
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
    // @GetMapping("/page/{pageNo}")
    // public ResponseEntity<Page<Product>> getPaginatedProducts(
    //         @PathVariable int pageNo,
    //         @RequestParam(defaultValue = "id") String sortField,
    //         @RequestParam(defaultValue = "asc") String sortDir) {
    //     int pageSize = 5;
    //     Page<Product> page = productService.findPaginated(pageNo, pageSize, sortField, sortDir);
    //     return ResponseEntity.ok(page);
    // }
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
    @GetMapping("/page")
    public ResponseEntity<Map<String,Object>> paginate(@RequestBody PageDTO rq) {
        int p = rq.getPage();
        int size = rq.getSize();
        Pageable pageable = PageRequest.of(p, size); 
        Page<Product> page = productService.findAll(pageable); 
        Map<String, Object> listPage = new HashMap<>();
            listPage.put("totalPages", page.getTotalPages());
            listPage.put("totalElements", page.getTotalElements());
            listPage.put("numberOfElements", page.getNumberOfElements());
            listPage.put("products", page.getContent());
        return ResponseEntity.ok(listPage); 
        
    }
}
