package com.example.doan.components;

import java.security.InvalidParameterException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.doan.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    @Value("${jwt.expiration}")
    private String expirationStr; 
    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(UserEntity user) {
        long expiration = Long.parseLong(expirationStr); 
        Map<String, Object> claims = new HashMap<>();
        this.generateSecretKey();
        claims.put("userName", user.getUsername());
        claims.put("role", user.getType().name()); 
        try {
            String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
            return token;
        } catch (Exception e) {
            throw new InvalidParameterException("Failed to generate token: " +e.getMessage());
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private String generateSecretKey(){
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }
    private Claims extractAlClaims(String token){
        return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }
    public <T> T extractClam(String token, Function<Claims, T> claimResolver) {
        final Claims claims = this.extractAlClaims(token);
        return claimResolver.apply(claims);
    }
    
    public boolean isTokenExpired(String token){
        Date expirationDate = this.extractClam(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }
    public String extractUsername(String token){
        return extractClam(token, Claims::getSubject);
    }
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
