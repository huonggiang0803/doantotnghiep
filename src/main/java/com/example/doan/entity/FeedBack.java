package com.example.doan.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedBack extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "product", nullable = false)
    @JsonIgnore
    private Product product;

    private String feedbackText;

    private int rating;
}
