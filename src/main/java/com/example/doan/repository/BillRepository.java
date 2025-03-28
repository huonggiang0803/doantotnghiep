package com.example.doan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.entity.Bill;
import com.example.doan.entity.Orders;

@Repository
public interface BillRepository  extends JpaRepository<Bill , Long> {
            boolean existsByOrder(Orders order);

}
