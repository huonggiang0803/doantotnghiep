package com.example.doan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {
    private String label;  // có thể là ngày, tháng, quý, năm
    private long count; 
}
