package com.example.doan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
    Optional<UserEntity> findByUserName(String username);
    Optional<UserEntity> findByEmail(String gmail);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
}
