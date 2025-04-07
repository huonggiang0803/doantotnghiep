package com.example.doan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.doan.dto.OrderStatisticsDTO;
import com.example.doan.entity.Orders;
import com.example.doan.entity.UserEntity;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUserId(UserEntity userId);

    @Query(value = "SELECT EXTRACT(DAY FROM o.created_at) AS day, SUM(o.total_price) " +
    "FROM orders o WHERE EXTRACT(YEAR FROM o.created_at) = :year AND EXTRACT(MONTH FROM o.created_at) = :month " +
    "GROUP BY day ORDER BY day", nativeQuery = true)
    List<Object[]> getDailyRevenue(@Param("year") int year, @Param("month") int month);

// Thống kê doanh thu theo tháng trong năm
    @Query(value = "SELECT EXTRACT(MONTH FROM o.created_at) AS month, SUM(o.total_price) " +
    "FROM orders o WHERE EXTRACT(YEAR FROM o.created_at) = :year " +
    "GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> getMonthlyRevenue(@Param("year") int year);

// Thống kê doanh thu theo quý trong năm
@Query(value = "SELECT EXTRACT(QUARTER FROM o.created_at) AS quarter, SUM(o.total_price) " +
    "FROM orders o WHERE EXTRACT(YEAR FROM o.created_at) = :year " +
    "GROUP BY quarter ORDER BY quarter", nativeQuery = true)
List<Object[]> getQuarterlyRevenue(@Param("year") int year);

// Thống kê doanh thu theo năm
@Query(value = "SELECT EXTRACT(YEAR FROM o.created_at) AS year, SUM(o.total_price) " +
    "FROM orders o GROUP BY year ORDER BY year", nativeQuery = true)
List<Object[]> getYearlyRevenue();

 // Thống kê số lượng đơn hàng theo ngày
@Query(value = "SELECT DATE(o.created_at) AS time, COUNT(*) AS orderCount " +
"FROM orders o WHERE EXTRACT(YEAR FROM o.created_at) = :year AND EXTRACT(MONTH FROM o.created_at) = :month " +
"GROUP BY time ORDER BY time", nativeQuery = true)
List<Object[]> getDailyOrderCount(@Param("year") int year, @Param("month") int month);

// Thống kê số lượng đơn hàng theo tháng
@Query(value = "SELECT EXTRACT(MONTH FROM o.created_at) AS time, COUNT(*) AS orderCount " +
"FROM orders o WHERE EXTRACT(YEAR FROM o.created_at) = :year " +
"GROUP BY time ORDER BY time", nativeQuery = true)
List<Object[]> getMonthlyOrderCount(@Param("year") int year);

// Thống kê số lượng đơn hàng theo quý
@Query(value = "SELECT EXTRACT(QUARTER FROM o.created_at) AS time, COUNT(*) AS orderCount " +
"FROM orders o WHERE EXTRACT(YEAR FROM o.created_at) = :year " +
"GROUP BY time ORDER BY time", nativeQuery = true)
List<Object[]> getQuarterlyOrderCount(@Param("year") int year);

// Thống kê số lượng đơn hàng theo năm
@Query(value = "SELECT EXTRACT(YEAR FROM o.created_at) AS time, COUNT(*) AS orderCount " +
"FROM orders o GROUP BY time ORDER BY time", nativeQuery = true)
List<Object[]> getYearlyOrderCount();

}