package com.example.doan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product extends AbstractEntity {

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = true, length = 10000)
    private String describe;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "brand")
    private String brand;

    @Column(name = "rating")
    private Double rating=0.0;

    @Column(name = "review_count")
    private Integer reviewCount=0;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<FeedBack> feedbacks = new ArrayList<>();

    @Column(name = "release_date") 
    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    // @Column(name = "product_status", nullable = false)
    // @Enumerated(EnumType.STRING)
    // private ProductStatus productStatus; 

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProductImage> images;

    @Column(name = "is_deleted")
    private Byte is_deleted = 0;

}
