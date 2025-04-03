package com.example.doan.dto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.doan.entity.Product;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm không được dài quá 255 ký tự")
    private String productName;
    
    private List<MultipartFile> photos;
    @Size(max = 10000, message = "Mô tả sản phẩm không được dài quá 10000 ký tự")
    private String describe;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId; 

    @NotBlank(message = "Thương hiệu không được để trống")
    private String brand;
    
    private String releaseDate;

    private String imageUrl; 

    @DecimalMin(value = "0.0", message = "Đánh giá không thể nhỏ hơn 0")
    @DecimalMax(value = "5.0", message = "Đánh giá không thể lớn hơn 5")
    private Double rating;

    @Min(value = 0, message = "Số lượng đánh giá không thể nhỏ hơn 0")
    private Integer reviewCount;

    @Builder.Default
    private List<ProductVariantDTO> variants = new ArrayList<>();
    private Byte status;

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.productName = product.getProductName();
        this.describe = product.getDescribe();
        this.categoryId = product.getCategory() != null ? product.getCategory().getId() : null; // Lấy id từ category
        this.brand = product.getBrand();
        this.releaseDate = null; // Nếu bạn không có thuộc tính releaseDate trong Product, hãy xử lý phù hợp
        this.imageUrl = null; // Nếu bạn không có thuộc tính imageUrl trong Product, hãy xử lý phù hợp
        this.rating = product.getRating();
        this.reviewCount = product.getReviewCount();
        this.status = null; // Nếu bạn không có thuộc tính status trong Product, hãy xử lý phù hợp
        this.variants = product.getVariants().stream()
                .map(ProductVariantDTO::new) // Ánh xạ từng variant sang ProductVariantDTO
                .collect(Collectors.toList());
    }
}
