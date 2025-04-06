package com.example.doan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.doan.dto.UserStatisticsDTO;
import com.example.doan.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
    Optional<UserEntity> findByUserName(String username);
    Optional<UserEntity> findByEmail(String gmail);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);

    @Query("SELECT TO_CHAR(u.createdAt, 'YYYY-MM-DD') AS day, COUNT(u) " +
    "FROM User u " +
    "WHERE EXTRACT(YEAR FROM u.createdAt) = :year AND EXTRACT(MONTH FROM u.createdAt) = :month " +
    "GROUP BY TO_CHAR(u.createdAt, 'YYYY-MM-DD') " +
    "ORDER BY day")
List<Object[]> getDailyUserCount(int year, int month);


@Query("SELECT EXTRACT(MONTH FROM u.createdAt) AS month, COUNT(u) " +
       "FROM User u " +
       "WHERE EXTRACT(YEAR FROM u.createdAt) = :year " +
       "GROUP BY EXTRACT(MONTH FROM u.createdAt) " +
       "ORDER BY month")
List<Object[]> getMonthlyUserCount(int year);


@Query("SELECT EXTRACT(QUARTER FROM u.createdAt) AS quarter, COUNT(u) " +
       "FROM User u " +
       "WHERE EXTRACT(YEAR FROM u.createdAt) = :year " +
       "GROUP BY EXTRACT(QUARTER FROM u.createdAt) " +
       "ORDER BY quarter")
List<Object[]> getQuarterlyUserCount(int year);

@Query("SELECT EXTRACT(YEAR FROM u.createdAt) AS year, COUNT(u) " +
       "FROM User u " +
       "GROUP BY EXTRACT(YEAR FROM u.createdAt) " +
       "ORDER BY year")
List<Object[]> getYearlyUserCount();


}