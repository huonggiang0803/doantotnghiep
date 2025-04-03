package com.example.doan.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.doan.dto.FeedBackDTO;
import com.example.doan.entity.FeedBack;
import com.example.doan.entity.Product;
import com.example.doan.entity.UserEntity;
import com.example.doan.repository.FeedbackReposi;
import com.example.doan.repository.ProductRepository;
import com.example.doan.repository.UserRepository;
@Service
public class FeedBackImple implements FeedBackService{
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/data/"; 
    @Autowired
    private FeedbackReposi feedbackRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    @Override
    public FeedBackDTO addFeedback(Long userId, Long productId, MultipartFile[] mediaUrls, String feedbackText, int rating) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại!"));
    
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));
    
        if (mediaUrls != null && mediaUrls.length > 5) {
            throw new RuntimeException("Số lượng ảnh tối đa là 5!");
        }
    
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    
        List<String> fileUrls = new ArrayList<>();
        if (mediaUrls != null) {
            for (MultipartFile file : mediaUrls) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File filePath = new File(UPLOAD_DIR + fileName);
                try {
                    file.transferTo(filePath);
                    fileUrls.add("/data/" + fileName);  
                } catch (IOException e) {
                    throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
                }
            }
        }
    
        FeedBack feedback = FeedBack.builder()
                .user(user)
                .product(product)
                .mediaUrls(fileUrls) 
                .feedbackText(feedbackText)
                .rating(rating)
                .build();
    
        feedbackRepository.save(feedback);
        updateProductRating(product);
        return mapToDTO(feedback);
    }
    
    private void updateProductRating(Product product) {
        Double avgRating = feedbackRepository.averageRatingByProductId(product.getId());
        Integer totalReviews = feedbackRepository.countByProductId(product.getId());
    
        product.setRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0); 
        product.setReviewCount(totalReviews);
    
        productRepository.save(product); 
    }
    private FeedBackDTO mapToDTO(FeedBack feedback) {
        return FeedBackDTO.builder()
        .id(feedback.getId())
        .userId(feedback.getUser().getId())
        .userName(feedback.getUser().getFullName())
        .productId(feedback.getProduct().getId())
        .productName(feedback.getProduct().getProductName())
        .mediaUrls(feedback.getMediaUrls()) // Lấy danh sách ảnh
        .feedbackText(feedback.getFeedbackText())
        .rating(feedback.getRating())
        .build();
    }
@Override
public List<FeedBackDTO> getFeedbackByProductId(Long productId) {
       List<FeedBack> feedbackList = feedbackRepository.findByProductId(productId);
        List<FeedBackDTO> feedbackDTOList = new ArrayList<>();

    for (FeedBack feedback : feedbackList) {
        FeedBackDTO feedbackDTO = mapToDTO(feedback);
        feedbackDTOList.add(feedbackDTO);
    }

    return feedbackDTOList;
}
}
