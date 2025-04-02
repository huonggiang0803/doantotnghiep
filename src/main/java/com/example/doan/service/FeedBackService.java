package com.example.doan.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.doan.dto.FeedBackDTO;

public interface FeedBackService {
    FeedBackDTO addFeedback(Long userId, Long productId, MultipartFile[] mediaUrls ,String feedbackText, int rating);
    List<FeedBackDTO> getFeedbackByProductId(Long productId);
} 