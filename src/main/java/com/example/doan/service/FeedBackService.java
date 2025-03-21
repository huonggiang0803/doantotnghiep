package com.example.doan.service;

import java.util.List;

import com.example.doan.dto.FeedBackDTO;

public interface FeedBackService {
    public FeedBackDTO addFeedback(Long userId, Long productId, String feedbackText, int rating);
    List<FeedBackDTO> getFeedbackByProductId(Long productId);
} 