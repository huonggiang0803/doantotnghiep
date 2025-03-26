package com.example.doan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.entity.BillItem;

@Repository
public interface BillItemRepository  extends JpaRepository<BillItem , Long>{

    
} 

