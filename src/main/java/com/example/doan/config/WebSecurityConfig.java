package com.example.doan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.doan.filters.JwtTokenFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(requests -> requests
            .requestMatchers("/api/users/register", "/api/users/login", "/api/users/forgot-password","/api/users/reset-password", "/api/users/verify-otp").permitAll()
            .requestMatchers("/api/products/**","/api/products/").permitAll()
            .requestMatchers("/api/ship/save","/api/ship/**").permitAll()
            .requestMatchers("/api/orders/**").permitAll()
            .requestMatchers("/api/payment/**").permitAll()
            .requestMatchers("/api/cart/**").authenticated()
            .requestMatchers("/api/users/delete/**").hasRole("ADMIN")
            .requestMatchers("/api/products/delete/**").hasRole("ADMIN")
            .requestMatchers("/api/users/all").hasRole("ADMIN")
            .requestMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN")
            .anyRequest().authenticated()
);
        return http.build();
    }
}
