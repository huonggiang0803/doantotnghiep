package com.example.doan.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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
    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @GetMapping("/profile")
    public ResponseEntity<UserEntity> getProfile(@AuthenticationPrincipal UserEntity currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập!");
        }
        return ResponseEntity.ok(currentUser);
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody UserEntity updatedUser, @AuthenticationPrincipal UserEntity currentUser) {
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập!");
        }

        if (!currentUser.getId().equals(updatedUser.getId())) {
            throw new IllegalArgumentException("Bạn không thể chỉnh sửa thông tin của người dùng khác!");
        }

        currentUser.setFullName(updatedUser.getFullName());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setAddess(updatedUser.getAddess());
        currentUser.setDateOfBirth(updatedUser.getDateOfBirth());
        currentUser.setPhone(updatedUser.getPhone());
        currentUser.setGender(updatedUser.getGender());

        us.save(currentUser); // Lưu thông tin người dùng
        return ResponseEntity.ok("Cập nhật thông tin thành công!");
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @RequestBody Map<String, String> passwordData,
            @AuthenticationPrincipal UserEntity currentUser) {
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác!");
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        us.save(currentUser);

        return ResponseEntity.ok("Đổi mật khẩu thành công!");
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
    public ResponseEntity<Map<String, String>> loginUser(@Valid @RequestBody LoginDTO dto) {
        try {
            String token = us.loginUser(dto.getUserName(), dto.getPassWord());
            UserEntity user = us.findByUsername(dto.getUserName());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getType().toString()); // Include the user's role
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//     @PostMapping("/loginUser")
//    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginDTO dto) {
//        try {
//           String token = us.loginUser(dto.getUserName(), dto.getPassWord());
//            return ResponseEntity.ok(token);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
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
        return ResponseEntity.ok("Tài khoản đã bị vô hiệu hóa");
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable long id,
            @Valid @RequestBody UserEntity updatedUser,
            @AuthenticationPrincipal UserEntity currentUser) {
        if (currentUser.getType() != UserType.ADMIN) {
            throw new AccessDeniedException("Bạn không có quyền chỉnh sửa thông tin người dùng!");
        }

        UserEntity user = us.findById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Người dùng không tồn tại!");
        }

        // Cập nhật các trường được phép
        user.setFullName(updatedUser.getFullName());
        user.setPhone(updatedUser.getPhone());
        user.setDateOfBirth(updatedUser.getDateOfBirth());
        user.setIs_deleted(updatedUser.getIs_deleted()); // Cập nhật trạng thái

        us.save(user);
        return ResponseEntity.ok("Cập nhật thông tin người dùng thành công!");
    }
}