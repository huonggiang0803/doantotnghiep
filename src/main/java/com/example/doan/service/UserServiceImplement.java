package com.example.doan.service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.example.doan.repository.CartItemRepository;
import com.example.doan.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.doan.components.JwtTokenUtil;
import com.example.doan.dto.RegisterDTOUser;
import com.example.doan.entity.UserEntity;
import com.example.doan.repository.CartRepository;
import com.example.doan.repository.OrderRepository;
import com.example.doan.repository.UserRepository;
import com.example.doan.status.UserType;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImplement implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AuthenticationManager authenticationManager;

    private static final Map<String, String> maOTP = new HashMap<>();
    private static final Map<String, Long> otpEx = new HashMap<>();
    private static final Map<String, Long> otpExpiry = new HashMap<>();

    @Override
    public String saveUser(RegisterDTOUser userRequestDTO, UserEntity currentUser) {
        if (!userRequestDTO.getPassWord().equals(userRequestDTO.getConfirmPassword())) {
            log.warn("Mật khẩu và xác nhận mật khẩu không khớp.");
            return "Mật khẩu và xác nhận mật khẩu không khớp!";
        }
        if (userRepository.existsByUserName(userRequestDTO.getUserName())) {
            log.warn("Tên đăng nhập đã tồn tại: {}", userRequestDTO.getUserName());
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
        }

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warn("Email đã được sử dụng: {}", userRequestDTO.getUserName());
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }

        UserType requestedRole = UserType.valueOf(userRequestDTO.getType().toUpperCase());
        if (requestedRole == UserType.ADMIN ) {
            log.warn("Người dùng không có quyền tạo ADMIN.");
            return "Bạn không có quyền tạo người dùng với vai trò ADMIN!";
        }
        UserEntity user = UserEntity.builder()
        .userName(userRequestDTO.getUserName())
        .fullName(userRequestDTO.getFullName())
        .password(passwordEncoder.encode(userRequestDTO.getPassWord()))
        .addess(userRequestDTO.getAddress())
        .email(userRequestDTO.getEmail())
        .dateOfBirth(userRequestDTO.getDateOfBirth())
        .phone(userRequestDTO.getPhone())
        .gender(userRequestDTO.getGender())
        .is_deleted((byte) 0)
        .type(UserType.valueOf(userRequestDTO.getType().toUpperCase()))
        .build();
        userRepository.save(user);
        try {
            String obj = "CHÀO MỪNG BẠN ĐẾN VỚI THẾ GIỚI MUA SẮM!";
            String body = "<p>Xin chào " + user.getFullName() + ",</p>"
            + "<p>Cảm ơn bạn đã đăng kí tài khoản tại hệ thống chúng tôi. Chúng tôi hoan nghênh khi có bạn đồng hành cùng chúng tôi!</p>"
            + "<p>Chúc bạn có trải nghiệm tuyệt vời khi mua sắm tại <b>UNET FASHION</b> chúng tôi!</p>"
            + "<p>Trân trọng!</p>";

            emailService.sendEmail(user.getEmail(), obj, body);
        } catch (MessagingException e) {
            return "Lỗi khi gửi email!";
        }
        return "Đăng ký thành công!";
    }
    public UserEntity findByUsername(String username) {
        return userRepository.findByUserName(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public String loginUser(String username, String password) {
        Optional<UserEntity> userO = userRepository.findByUserName(username);
        if (userO.isEmpty()) {
            throw new RuntimeException("Người dùng không tồn tại!");
        }
        UserEntity user = userO.get();
        if (user.getIs_deleted() == 1) {
            throw new RuntimeException("Tài khoản của bạn đã bị vô hiệu hóa, vui lòng liên hệ hỗ trợ!");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) { 
            throw new RuntimeException("Mật khẩu không chính xác!");
        }
        UserEntity existingUser = userO.get();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, existingUser.getAuthorities());
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public String forgotPassword(String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không tồn tại!");
        }
        UserEntity user = userOpt.get();
        String otp = taoMaOTP();
        maOTP.put(user.getEmail(), otp);
        otpExpiry.put(user.getEmail(), System.currentTimeMillis() + (1 * 60 * 1000));
        try {
            String subject = "Mã OTP và liên kết đặt lại mật khẩu";
            String body = "Mã OTP của bạn là: " + otp + ".\n"
                    + "Mã OTP có hiệu lực trong 1 phút.";
            emailService.sendEmail(user.getEmail(), subject, body);
            return "OTP đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư.";
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi gửi email!");
        }
    }
    private String linkReset(String userName) {
        return "http://localhost:8080/api/users/register";
    }
    private String taoMaOTP() {
        int otpLength = 6;
        Random random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10)); 
        }
        
        return otp.toString();
    }
    public String xacMinhOtp(String email, String otp) {
        if (!maOTP.containsKey(email)) {
            return "OTP không tồn tại hoặc đã hết hạn!";
        }
        if (System.currentTimeMillis() > otpExpiry.get(email)) {
            maOTP.remove(email);
            otpExpiry.remove(email);
            return "OTP đã hết hạn!";
        }
    
        if (!maOTP.get(email).equals(otp)) {
            return "OTP không chính xác!";
        }
        maOTP.remove(email);
        otpExpiry.remove(email);
        
        return "OTP hợp lệ! Bạn có thể đặt lại mật khẩu.";
    }

    @Override
public void deleteUser(long id, UserEntity currentUser) {
    Optional<UserEntity> userToDelete = userRepository.findById(id);
    if (userToDelete.isEmpty()) {
        throw new RuntimeException("User not found");
    }
    if (currentUser.getType() != UserType.ADMIN) {
        throw new RuntimeException("Bạn không có quyền xóa người dùng!");
    }
    UserEntity user = userToDelete.get();
    user.setIs_deleted((byte) 1);
    userRepository.save(user);
}
    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll(); 
    }
    @Override
    public String resetPassword(  String email,String newPassword) {
      
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "Email không tồn tại!";
        }
        UserEntity user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return "Mật khẩu đã được thay đổi thành công!";
    }
    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public String changePassword(UserEntity currentUser, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác!");
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        return "Đổi mật khẩu thành công!";
    }

    @Override
    public UserEntity findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại!"));
    }
}
