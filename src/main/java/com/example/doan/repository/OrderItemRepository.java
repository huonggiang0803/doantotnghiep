package com.example.doan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.doan.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

}
