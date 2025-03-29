package com.example.doan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@Table(name = "category")
public class Category extends AbstractEntity {
    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName; 
    
    @Column(name = "gender")
    private String gender;

    @Column(name = "description")
    private String description; 


    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Product> products;  

    @Column(name = "is_deleted")
    private Byte is_deleted = 0; 

}