package com.example.doan.controller;

import com.example.doan.dto.OrderStatisticsDTO;
import com.example.doan.dto.RevenueDTO;
import com.example.doan.service.TKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics/revenue")
public class TKController {

    @Autowired
    private TKService statisticsService;

    // /api/statistics/revenue/daily?year=2024&month=4
    @GetMapping("/daily")
    public ResponseEntity<List<RevenueDTO>> getDaily(@RequestParam int year, @RequestParam int month) {
        return ResponseEntity.ok(statisticsService.getDailyRevenue(year, month));
    }

    // /api/statistics/revenue/monthly?year=2024
    @GetMapping("/monthly")
    public ResponseEntity<List<RevenueDTO>> getMonthly(@RequestParam int year) {
        return ResponseEntity.ok(statisticsService.getMonthlyRevenue(year));
    }

    // /api/statistics/revenue/quarterly?year=2024
    @GetMapping("/quarterly")
    public ResponseEntity<List<RevenueDTO>> getQuarterly(@RequestParam int year) {
        return ResponseEntity.ok(statisticsService.getQuarterlyRevenue(year));
    }

    // /api/statistics/revenue/yearly
    @GetMapping("/yearly")
    public ResponseEntity<List<RevenueDTO>> getYearly() {
        return ResponseEntity.ok(statisticsService.getYearlyRevenue());
    }

    @GetMapping("/SLdaily")
    public ResponseEntity<List<OrderStatisticsDTO>> getDailyOrderStatistics(@RequestParam int year, @RequestParam int month) {
        return ResponseEntity.ok(statisticsService.getDailyOrderCount(year, month));
    }

    // Thống kê số lượng đơn hàng theo tháng
    // /api/statistics/orders/monthly?year=2024
    @GetMapping("/SLmonthly")
    public ResponseEntity<List<OrderStatisticsDTO>> getMonthlyOrderStatistics(@RequestParam int year) {
        return ResponseEntity.ok(statisticsService.getMonthlyOrderCount(year));
    }

    // Thống kê số lượng đơn hàng theo quý
    // /api/statistics/revenue/quarterly?year=2024
    @GetMapping("/SLquarterly")
    public ResponseEntity<List<OrderStatisticsDTO>> getQuarterlyOrderStatistics(@RequestParam int year) {
        return ResponseEntity.ok(statisticsService.getQuarterlyOrderCount(year));
    }

    // Thống kê số lượng đơn hàng theo năm
    // /api/statistics/revenue/yearly
    @GetMapping("/SLyearly")
    public ResponseEntity<List<OrderStatisticsDTO>> getYearlyOrderStatistics() {
        return ResponseEntity.ok(statisticsService.getYearlyOrderCount());
    }
}