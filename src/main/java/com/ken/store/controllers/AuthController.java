package com.ken.store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ken.store.config.JwtConfig;
import com.ken.store.dtos.JwtResponse;
import com.ken.store.dtos.LoginRequest;
import com.ken.store.dtos.UserDto;
import com.ken.store.mappers.UserMapper;
import com.ken.store.repositories.UserRepository;
import com.ken.store.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authorizationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        // If user validation passes, give the user a token
        authorizationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        var accesstoken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // make sure refreshToken cannot be stolen by JavaScript
        var cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true); 
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); // cookie expires in ? days
        cookie.setSecure(true); // only be sent over https connections
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accesstoken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
        @CookieValue(value = "refreshToken") String refreshToken
    ) {
        if (!jwtService.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var userId = jwtService.getUserIdFromToken(refreshToken);
        var user = userRepository.findById(userId).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(new JwtResponse(accessToken));
    }
    

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.toDto(user));
    }
    


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
