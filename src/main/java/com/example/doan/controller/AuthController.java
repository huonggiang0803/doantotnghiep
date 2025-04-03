package com.example.doan.controller;

import com.example.doan.entity.UserEntity;
import com.example.doan.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String home(@AuthenticationPrincipal OAuth2User oauth2User, HttpServletResponse response) throws IOException {
        String email = oauth2User.getAttribute("email");
        Optional<UserEntity> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return "Welcome back, " + user.get().getFullName();
        } else {
            response.sendRedirect("/oauth2/authorization/google?prompt=login"); 
            return null; 
        }
    }

    @GetMapping("/logout")
public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
    SecurityContextHolder.clearContext();
    request.getSession().invalidate();
    response.sendRedirect("/oauth2/authorization/google"); 
    return null;
}
}


