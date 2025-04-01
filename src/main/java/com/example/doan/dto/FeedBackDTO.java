package com.example.doan.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedBackDTO {
    private Long id;
    private Long userId;
    private String userName; 
    private Long productId;
    private String productName; 
    private List<String> mediaUrls; 
    private String feedbackText;
    private int rating; 
}
