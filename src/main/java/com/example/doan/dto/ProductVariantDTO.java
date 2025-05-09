package com.example.doan.dto;

import com.example.doan.entity.ProductVariant;
import com.example.doan.status.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDTO {
     @NotBlank(message = "Kích thước không được để trống")
    private String size;

    @NotBlank(message = "Màu không được để trống")
    private String color;

    @NotBlank(message = "Chất liệu không được để trống")
    private String material;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @Positive(message = "Giá sản phẩm phải lớn hơn 0")
    private Double price;

    @NotNull(message = "Số lượng tồn kho sản phẩm không được để trống")
    @Min(value = 0, message = "Số lượng không thể nhỏ hơn 0")
    private Integer stock;

    private Double discountPercentage;

    @PositiveOrZero(message = "Giá khuyến mãi phải >= 0")
    private Double promotionalPrice;

    private ProductStatus productStatus;

    public ProductVariantDTO(ProductVariant variant) {
        this.size = variant.getSize();
        this.color = variant.getColor();
        this.material = variant.getMaterial();
        this.price = variant.getPrice();
        this.stock = variant.getStock();
        this.discountPercentage = variant.getDiscountPercentage();
        this.promotionalPrice = variant.getPromotionalPrice();
        this.productStatus = variant.getProductStatus();
    }
}
