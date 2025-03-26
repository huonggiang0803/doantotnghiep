package com.example.doan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.doan.dto.ForgotPasswordDTO;
import com.example.doan.dto.LoginDTO;
import com.example.doan.dto.RegisterDTOUser;
import com.example.doan.entity.UserEntity;
import com.example.doan.service.UserService;
import com.example.doan.status.UserType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class RegisterUserController {
    @Autowired
    private UserService us;

    @GetMapping("/user")
    public String showRegisterForm() {
        return "register"; 
    }
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserEntity>> getAllUsers(@AuthenticationPrincipal UserEntity currentUser) {
    if (currentUser == null || currentUser.getType() != UserType.ADMIN) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem danh sách người dùng!");
    }
    List<UserEntity> users = us.getAllUsers();
    return ResponseEntity.ok(users);
}

@PostMapping("/registerUser")
public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDTOUser dto, @AuthenticationPrincipal UserEntity currentUser) {
    try {
        String result = us.saveUser(dto, currentUser);
        return ResponseEntity.ok(result);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

     @PostMapping("/loginUser")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginDTO dto) {
        try {
           String token = us.loginUser(dto.getUserName(), dto.getPassWord());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        String result = us.forgotPassword(dto.getEmail());
        return ResponseEntity.ok(result);
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        String response = us.xacMinhOtp(email, otp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        String response = us.resetPassword(email, newPassword);
        return ResponseEntity.ok(response);
    }
   @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
    // Lấy thông tin người dùng hiện tại từ SecurityContext
    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    UserEntity currentUser = us.findByUsername(userDetails.getUsername()); 

    if (currentUser.getType() != UserType.ADMIN) {
        throw new AccessDeniedException("Bạn không có quyền xóa người dùng!");
    }
    us.deleteUser(id, currentUser);
    return ResponseEntity.ok("User deleted successfully");
}
}