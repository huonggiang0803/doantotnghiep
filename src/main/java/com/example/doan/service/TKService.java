package com.example.doan.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.doan.dto.OrderStatisticsDTO;
import com.example.doan.dto.RevenueDTO;
import com.example.doan.repository.OrderRepository;

@Service
public class TKService {
    @Autowired
    private OrderRepository ordersRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<RevenueDTO> getDailyRevenue(int year, int month) {
        return convertToDTO(orderRepository.getDailyRevenue(year, month));
    }

    public List<RevenueDTO> getMonthlyRevenue(int year) {
        return convertToDTO(orderRepository.getMonthlyRevenue(year));
    }

    public List<RevenueDTO> getQuarterlyRevenue(int year) {
        return convertToDTO(orderRepository.getQuarterlyRevenue(year));
    }

    public List<RevenueDTO> getYearlyRevenue() {
        return convertToDTO(orderRepository.getYearlyRevenue());
    }

    private List<RevenueDTO> convertToDTO(List<Object[]> results) {
        List<RevenueDTO> list = new ArrayList<>();
        for (Object[] row : results) {
            int label = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            list.add(new RevenueDTO(label, total));
        }
        return list;
    }
    private List<OrderStatisticsDTO> convertToOrderStatisticsDTO(List<Object[]> results) {
        List<OrderStatisticsDTO> list = new ArrayList<>();
        for (Object[] row : results) {
            String label = row[0].toString(); // có thể là ngày/tháng/quý/năm
            long count = ((Number) row[1]).longValue();
            list.add(new OrderStatisticsDTO(label, count));
        }
        return list;
    }

    public List<OrderStatisticsDTO> getDailyOrderCount(int year, int month) {
        return convertToOrderStatisticsDTO(orderRepository.getDailyOrderCount(year, month));
    }

    public List<OrderStatisticsDTO> getMonthlyOrderCount(int year) {
        return convertToOrderStatisticsDTO(orderRepository.getMonthlyOrderCount(year));
    }

    public List<OrderStatisticsDTO> getQuarterlyOrderCount(int year) {
        return convertToOrderStatisticsDTO(orderRepository.getQuarterlyOrderCount(year));
    }

    public List<OrderStatisticsDTO> getYearlyOrderCount() {
        return convertToOrderStatisticsDTO(orderRepository.getYearlyOrderCount());
    }
}