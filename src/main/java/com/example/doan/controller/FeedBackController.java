package com.example.doan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.doan.dto.FeedBackDTO;
import com.example.doan.entity.UserEntity;
import com.example.doan.service.FeedBackService;

@RestController
@RequestMapping("/api/feedback")
public class FeedBackController {
    @Autowired
    FeedBackService feedBackService;
   @PostMapping("/add")
    public ResponseEntity<?> addFeedback(
        @RequestParam("productId") Long productId,
        @RequestParam(value = "files", required = false) MultipartFile[] files, // Nhận danh sách file
        @RequestParam("feedbackText") String feedbackText,
        @RequestParam("rating") int rating) {

        Long userId = getCurrentUserId(); // Lấy user đang đăng nhập
        FeedBackDTO createdFeedback = feedBackService.addFeedback(userId, productId, files, feedbackText, rating);
        return ResponseEntity.ok(createdFeedback);
    }
@GetMapping("/product/{productId}")
public ResponseEntity<List<FeedBackDTO>> getFeedbackByProduct(@PathVariable Long productId) {
    List<FeedBackDTO> feedbacks = feedBackService.getFeedbackByProductId(productId);
    return ResponseEntity.ok(feedbacks);
}

    private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserEntity user) {
        return user.getId();
    }
    throw new RuntimeException("Người dùng chưa đăng nhập!");
}
}
