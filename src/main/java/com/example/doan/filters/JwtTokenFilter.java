package com.example.doan.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.*;

import com.example.doan.components.JwtTokenUtil;
import com.example.doan.entity.UserEntity;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;;
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter{
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isByPassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                final String token = authHeader.substring(7);
                final String userName = jwtTokenUtil.extractUsername(token);
                if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserEntity userDetails = (UserEntity) userDetailsService.loadUserByUsername(userName);
                    if (jwtTokenUtil.validateToken(token, userDetails)) {
                        String role = jwtTokenUtil.extractClam(token, claims -> claims.get("role", String.class));
                        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities
                        );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
        }
    } private boolean isByPassToken(@NonNull HttpServletRequest request){
        final List<Pair<String,String>> bypassTokens = Arrays.asList(
            Pair.of("/api/products","GET"),
            Pair.of("/api/users/register","POST"),
            Pair.of("/api/users/login","POST")
        );
        for(Pair<String,String> bypassToken: bypassTokens){
            if (request.getServletPath().contains(bypassToken.getFirst()) && 
                request.getMethod().equals(bypassToken.getSecond())){
                   return true;
            }
        }
        return false;
    }

}
