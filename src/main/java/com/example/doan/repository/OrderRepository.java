package com.example.doan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.entity.Orders;
import com.example.doan.entity.UserEntity;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long>{
    List<Orders> findByUserId(UserEntity userId);
}
