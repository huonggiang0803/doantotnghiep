package com.example.doan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.dto.UserStatisticsDTO;
import com.example.doan.service.UserStatisticsService;

@RestController
@RequestMapping("/api/statistics/users")
public class TKUser {
    @Autowired
    private UserStatisticsService userStatisticsService;

    @GetMapping("/daily")
    public ResponseEntity<List<UserStatisticsDTO>> getDaily(@RequestParam int year, @RequestParam int month) {
        return ResponseEntity.ok(userStatisticsService.getDailyUserCount(year, month));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<UserStatisticsDTO>> getMonthly(@RequestParam int year) {
        return ResponseEntity.ok(userStatisticsService.getMonthlyUserCount(year));
    }

    @GetMapping("/quarterly")
    public ResponseEntity<List<UserStatisticsDTO>> getQuarterly(@RequestParam int year) {
        return ResponseEntity.ok(userStatisticsService.getQuarterlyUserCount(year));
    }

    @GetMapping("/yearly")
    public ResponseEntity<List<UserStatisticsDTO>> getYearly() {
        return ResponseEntity.ok(userStatisticsService.getYearlyUserCount());
    }
}
