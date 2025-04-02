package com.example.doan.repository;

import com.example.doan.dto.InforShippingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.entity.InforShipping;

import java.util.Optional;

@Repository
public interface InforShipRepository extends JpaRepository<InforShipping, Long>{
    Optional<InforShipping> findByIsDefaultTrue();
}
