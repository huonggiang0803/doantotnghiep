package com.example.doan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.doan.filters.JwtTokenFilter;
import com.example.doan.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Autowired
    private CustomOAuth2UserService oauthUserService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(requests -> requests
            .requestMatchers("/api/users/registerUser", "/api/users/loginUser", "/api/users/forgot-password","/api/users/reset-password", "/api/users/verify-otp").permitAll()
            .requestMatchers("/api/products/**","/api/products/","/api/products/page").permitAll()
            .requestMatchers("/api/ship/save","/api/ship/**").permitAll()
            .requestMatchers("/api/orders/**").permitAll()
            .requestMatchers("/api/payment/**").permitAll()
            .requestMatchers("/api/feedback/**").permitAll()
            .requestMatchers("/api/cart/**").authenticated()
            .requestMatchers("/api/users/delete/**").hasRole("ADMIN")
            .requestMatchers("/api/products/delete/**").hasRole("ADMIN")
            .requestMatchers("/api/users/all").hasRole("ADMIN")
            .requestMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers("/product-photo/**").permitAll() 
            .requestMatchers("/api/bills/**").permitAll()
            .requestMatchers("/auth/**").authenticated()
            .requestMatchers("/", "/login", "/oauth2/**").permitAll()
            .anyRequest().authenticated()
            
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo.userService(oauthUserService))
            )
            .logout(logout -> logout
                .logoutUrl("/logout")  
                .logoutSuccessUrl("/oauth2/authorization/google")  
                .invalidateHttpSession(true) 
                .clearAuthentication(true)  
                .permitAll()
            );
        return http.build();
    }
}
