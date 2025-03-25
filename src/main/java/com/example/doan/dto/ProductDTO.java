package com.example.doan.dto;
import java.util.ArrayList;
import java.util.List;

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

    private List<ProductVariantDTO> variants = new ArrayList<>();;
}
