package com.ken.store.services;

import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ken.store.config.JwtConfig;
import com.ken.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class JwtService {

    private final JwtConfig jwtConfig;

    public String generateAccessToken(User user) {
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private String generateToken(User user, final long tokenExpiration) {
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("name", user.getName())
            .claim("role", user.getRole())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
            .signWith(jwtConfig.getSecretKey())
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            var claims = getClaims(token);
            boolean isValid = claims.getExpiration().after(new Date());
            return isValid;
        } catch (JwtException e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(jwtConfig.getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
