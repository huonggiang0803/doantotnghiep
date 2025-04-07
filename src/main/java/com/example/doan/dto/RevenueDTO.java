package com.example.doan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor         
@AllArgsConstructor 
public class RevenueDTO {
    private int label; 
    private double totalRevenue;
}
