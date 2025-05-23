package com.example.doan.service;

import java.util.List;

import com.example.doan.dto.RegisterDTOUser;
import com.example.doan.entity.UserEntity;

public interface UserService {
    String saveUser(RegisterDTOUser userRequestDTO, UserEntity userEntity);
    String loginUser(String username, String password);
    String forgotPassword(String email); 
    void deleteUser(long id, UserEntity currentUser);
    UserEntity findByUsername(String username) ;
    List<UserEntity> getAllUsers();
    String resetPassword(  String email,String newPassword) ;
    String xacMinhOtp(String email, String otp);
    UserEntity save(UserEntity user);
    String changePassword(UserEntity currentUser, String oldPassword, String newPassword);
    UserEntity findById(long id);
}
