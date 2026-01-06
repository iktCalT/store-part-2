package com.ken.store.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ken.store.dtos.UserDto;
import com.ken.store.exceptions.PasswordIncorrectException;
import com.ken.store.exceptions.UserNotFoundException;
import com.ken.store.mappers.UserMapper;
import com.ken.store.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Transactional
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto login(String email, String password) {
        var user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }

        // NOTE: use maches rather than equals!!!!!
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordIncorrectException();
        }

        return userMapper.toDto(user);
    }

}
