package com.ken.store.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ken.store.dtos.LoginRequest;
import com.ken.store.dtos.UserDto;
import com.ken.store.exceptions.PasswordIncorrectException;
import com.ken.store.exceptions.UserNotFoundException;
import com.ken.store.services.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequest request) {
        var userDto = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(userDto);
    }
    

    // To prevent others from knowing which email is registered 
    @ExceptionHandler({UserNotFoundException.class, PasswordIncorrectException.class})
    public ResponseEntity<Map<String, String>> handlePasswordIncorrect() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            Map.of("error", "Incorrect email or password.")
        );
    }
}
