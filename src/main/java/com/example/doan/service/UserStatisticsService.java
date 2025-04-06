package com.example.doan.service;

import com.example.doan.dto.UserStatisticsDTO;
import com.example.doan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserStatisticsService {

    @Autowired
    private UserRepository userRepository;

    // Chuyển đổi kết quả từ List<Object[]> thành List<UserStatisticsDTO>
    private List<UserStatisticsDTO> convertToUserStatisticsDTO(List<Object[]> results) {
        List<UserStatisticsDTO> list = new ArrayList<>();
        for (Object[] row : results) {
            String label = row[0].toString(); // có thể là ngày/tháng/quý/năm
            long count = ((Number) row[1]).longValue();
            list.add(new UserStatisticsDTO(label, count));
        }
        return list;
    }

    // Thống kê số lượng người dùng theo ngày
    public List<UserStatisticsDTO> getDailyUserCount(int year, int month) {
        return convertToUserStatisticsDTO(userRepository.getDailyUserCount(year, month));
    }

    // Thống kê số lượng người dùng theo tháng
    public List<UserStatisticsDTO> getMonthlyUserCount(int year) {
        return convertToUserStatisticsDTO(userRepository.getMonthlyUserCount(year));
    }

    // Thống kê số lượng người dùng theo quý
    public List<UserStatisticsDTO> getQuarterlyUserCount(int year) {
        return convertToUserStatisticsDTO(userRepository.getQuarterlyUserCount(year));
    }

    // Thống kê số lượng người dùng theo năm
    public List<UserStatisticsDTO> getYearlyUserCount() {
        return convertToUserStatisticsDTO(userRepository.getYearlyUserCount());
    }
}
