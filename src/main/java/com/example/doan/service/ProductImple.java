package com.example.doan.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import com.example.doan.controller.FileUploadUtil;
import com.example.doan.dto.ProductDTO;
import com.example.doan.dto.ProductVariantDTO;
import com.example.doan.entity.Category;
import com.example.doan.entity.Product;
import com.example.doan.entity.ProductImage;
import com.example.doan.entity.ProductVariant;
import com.example.doan.repository.CategoryRepository;
import com.example.doan.repository.ProductImageRepository;
import com.example.doan.repository.ProductRepository;
import com.example.doan.repository.ProductVariantReposi;
import com.example.doan.status.ProductStatus;
@Service
public class ProductImple implements ProductService{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private ProductVariantReposi productVariantReposi;
   @Override

   public Long saveProduct(ProductDTO productDTO, MultipartFile imageFile) {
    Category category = categoryRepository.findById(productDTO.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category không tồn tại"));
            Date releaseDate = null;
            if (productDTO.getReleaseDate() != null && !productDTO.getReleaseDate().isEmpty()) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    releaseDate = dateFormat.parse(productDTO.getReleaseDate());
                } catch (ParseException e) {
                    throw new RuntimeException("Định dạng ngày không hợp lệ. Định dạng đúng: yyyy-MM-dd");
                }
            }
    Product product = Product.builder()
            .productName(productDTO.getProductName())
            .describe(productDTO.getDescribe())
            .category(category)
            .brand(productDTO.getBrand())
            .rating(productDTO.getRating())
            .reviewCount(productDTO.getReviewCount())
            .releaseDate(releaseDate)   
            .is_deleted((byte) 0)
            // .productStatus(ProductStatus.OUT_OF_STOCK)   
            .build();
        //    product.updateStatus();
            product = productRepository.save(product);

            List<ProductVariant> variants = new ArrayList<>();
            for (ProductVariantDTO variantDTO : productDTO.getVariants()) {
             ProductVariant variant = ProductVariant.builder()
                .product(product)
                .color(variantDTO.getColor())
                .size(variantDTO.getSize())
                .material(variantDTO.getMaterial())
                .price(variantDTO.getPrice())
                .stock(variantDTO.getStock())
                .discountPercentage(variantDTO.getDiscountPercentage())
                .productStatus(ProductStatus.AVAILABLE)
                .promotionalPrice(variantDTO.getPromotionalPrice())
                .build();
        variants.add(variant);
    }
    productVariantReposi.saveAll(variants);
    // product.updateStatus();
    productRepository.save(product);

    List<ProductImage> imageUrl = new ArrayList<>();
    if (imageFile != null && !imageFile.isEmpty()) {
        try {
            String fileName = imageFile.getOriginalFilename();
            FileUploadUtil.saveFile("product-photo", fileName, imageFile);

            ProductImage image = new ProductImage();
            image.setFileName("/product-photo/" + fileName);
            image.setProduct(product); 
            imageUrl.add(image);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu ảnh: " + e.getMessage());
        }
    }
    productImageRepository.saveAll(imageUrl);
    return product.getId();
}
    @Override
public Product getProductById(long id) {
    System.out.println("Tìm sản phẩm với ID: " + id);
    return productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException(" Không tìm thấy sản phẩm với ID: " + id));
}
    @Override
    public void deleteProductByID(long id) {
        Product product = productRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));
    
        product.setIs_deleted((byte) 1); 
        productRepository.save(product);
    }
    @Override
    public List<Product> getAllProduct(String keyword) {
        if (keyword != null){
            return productRepository.search(keyword);
        }
        else{
            return (List<Product>) productRepository.findAll();
        }
    }
    @Override
    public Page<Product> findPaginated(int pageNo, int pageSize, String sortFileld, String sortDir) {
       Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                    Sort.by(sortFileld).ascending() : 
                    Sort.by(sortFileld).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return productRepository.findAll(pageable);
    }

    public void updateProduct(Long id, ProductDTO productDTO, List<MultipartFile> photos) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

                Date releaseDate = null;
                if (productDTO.getReleaseDate() != null && !productDTO.getReleaseDate().isEmpty()) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        releaseDate = dateFormat.parse(productDTO.getReleaseDate());
                    } catch (ParseException e) {
                        throw new RuntimeException("Định dạng ngày không hợp lệ. Định dạng đúng: yyyy-MM-dd");
                    }
                }
        product.setProductName(productDTO.getProductName());
        product.setDescribe(productDTO.getDescribe());
        product.setRating(productDTO.getRating());
        product.setReviewCount(productDTO.getReviewCount());
        product.setReleaseDate(releaseDate);
        productRepository.save(product);

        List<ProductVariant> oProductVariants = productVariantReposi.findByProductId(id);
        productVariantReposi.deleteAll(oProductVariants);

        List<ProductVariant> newVariants = new ArrayList<>();
    for (ProductVariantDTO variantDTO : productDTO.getVariants()) {
        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .color(variantDTO.getColor())
                .size(variantDTO.getSize())
                .material(variantDTO.getMaterial())
                .price(variantDTO.getPrice())
                .stock(variantDTO.getStock())
                .discountPercentage(variantDTO.getDiscountPercentage())
                .promotionalPrice(variantDTO.getPromotionalPrice())
                .build();
        newVariants.add(variant);
    }
    productVariantReposi.saveAll(newVariants);
    // product.updateStatus();
    productRepository.save(product);

        if (photos != null && !photos.isEmpty()) {
            List<ProductImage> oldImages = productImageRepository.findByProductId(id);
            
            for (ProductImage oldImage : oldImages) {
                String oldFilePath = oldImage.getFileName();
                FileUploadUtil.deleteFile(oldFilePath); 
            }
                productImageRepository.deleteAll(oldImages);
    
            List<ProductImage> newImages = new ArrayList<>();
                for (MultipartFile photo : photos) {
                if (!photo.isEmpty()) {
                    try {
                        String fileName = photo.getOriginalFilename();
                        FileUploadUtil.saveFile("product-photo", fileName, photo);
                        
                        ProductImage image = new ProductImage();
                        image.setFileName("/product-photo/" + fileName);
                        image.setProduct(product);
                        newImages.add(image);
    
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi lưu ảnh: " + e.getMessage());
                    }
                }
            }
                productImageRepository.saveAll(newImages);
        }
    }
    @Override
    public List<ProductDTO> geProductDTOs(String size, String color, Double minPrice, Double maxPrice) {
        List<Product> products = productRepository.findFilteredProducts(size, color, minPrice, maxPrice);
        return converToDTOs(products);
    }

    public List<ProductDTO> converToDTOs(List<Product> products){
        List<ProductDTO> productDTOs = new ArrayList<>();
    for (Product product : products) {
        List<ProductImage> images = productImageRepository.findByProductId(product.getId());
        List<String> imageUrls = new ArrayList<>();
        
        for (ProductImage image : images) {
            imageUrls.add(image.getFileName()); 
        }
        List<ProductVariantDTO> variantDTOs = new ArrayList<>();
        for (ProductVariant variant : product.getVariants()) {
            ProductVariantDTO variantDTO = ProductVariantDTO.builder()
                    .color(variant.getColor())
                    .size(variant.getSize())
                    .material(variant.getMaterial())
                    .price(variant.getPrice())
                    .discountPercentage(variant.getDiscountPercentage())
                    .promotionalPrice(variant.getPromotionalPrice())
                    .build();
            variantDTOs.add(variantDTO);
        }

        ProductDTO productDTO = ProductDTO.builder()
        .id(product.getId())
        .productName(product.getProductName())
        .describe(product.getDescribe())
        .categoryId(product.getCategory().getId())
        .brand(product.getBrand())
        .rating(product.getRating())
        .reviewCount(product.getReviewCount())
        .imageUrl(imageUrls.isEmpty() ? null : imageUrls.get(0))
        .variants(variantDTOs) 
        .build();

        productDTOs.add(productDTO);
    }
    return productDTOs;

    }
    @Override
    public List<ProductImage> getProductImages(long productId) {
        return productImageRepository.findByProductId(productId);
}
    @Override
    public void addImagesToProduct(Long productId, List<MultipartFile> imageFiles) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile file : imageFiles) {
            String fileName = file.getOriginalFilename();
            ProductImage image = new ProductImage();
            image.setFileName(fileName);
            image.setProduct(product);
            productImages.add(image);
        }
        productImageRepository.saveAll(productImages);
    }
    public Page<Product> findAll(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
    }  
    
}
