package com.example.doan.service.mapper;

import com.example.doan.dto.ProductDTO;
import com.example.doan.dto.ProductVariantDTO;
import com.example.doan.entity.Product;
import com.example.doan.entity.ProductVariant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toProductDTO(Product product);

    ProductVariantDTO toProductVariantDTO(ProductVariant variant);
}
