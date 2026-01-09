package com.ken.store.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ken.store.auth.dtos.JwtResponse;
import com.ken.store.auth.dtos.LoginRequest;
import com.ken.store.auth.exceptions.RefreshTokenExpiredException;
import com.ken.store.auth.services.AuthService;
import com.ken.store.users.dtos.UserDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public JwtResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        var accessToken = authService.login(request, response);
        return new JwtResponse(accessToken.toString());
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(
        @CookieValue(value = "refreshToken") String refreshToken
    ) {
        var accessToken = authService.refresh(refreshToken);
        return new JwtResponse(accessToken.toString());
    }
    
    @GetMapping("/me")
    public UserDto me() {
        return authService.me();
    }
    
    @ExceptionHandler({BadCredentialsException.class, RefreshTokenExpiredException.class})
    public ResponseEntity<Void> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
