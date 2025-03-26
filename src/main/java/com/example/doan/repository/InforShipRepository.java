package com.example.doan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.entity.InforShipping;

@Repository
public interface InforShipRepository extends JpaRepository<InforShipping, Long>{

}
