package com.ken.store.auth.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.ken.store.auth.config.JwtConfig;
import com.ken.store.auth.dtos.LoginRequest;
import com.ken.store.auth.exceptions.RefreshTokenExpiredException;
import com.ken.store.users.dtos.UserDto;
import com.ken.store.users.entities.User;
import com.ken.store.users.exceptions.UserNotFoundException;
import com.ken.store.users.mappers.UserMapper;
import com.ken.store.users.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    public Jwt login(LoginRequest request, HttpServletResponse response) {
        // If user validation passes, give the user a token
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // make sure refreshToken cannot be stolen by JavaScript
        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true); 
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); // cookie expires in ? days
        cookie.setSecure(true); // only be sent over https connections
        response.addCookie(cookie);

        return accessToken;
    }

    public Jwt refresh(String refreshToken) {
        var jwt = jwtService.parse(refreshToken);
        if (jwt == null || jwt.isExpired()) {
            throw new RefreshTokenExpiredException();
        }

        var user = userRepository.findById(jwt.getUserId()).orElseThrow();
        return jwtService.generateAccessToken(user);
    }

    public UserDto me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            // Handled by global exception handler
            throw new UserNotFoundException();
        }

        return userMapper.toDto(user);
    }

}
