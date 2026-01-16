package com.ken.store.users.services;

import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ken.store.auth.services.AuthService;
import com.ken.store.users.dtos.ChangePasswordRequest;
import com.ken.store.users.dtos.RegisterUserRequest;
import com.ken.store.users.dtos.UpdateUserRequest;
import com.ken.store.users.dtos.UserDto;
import com.ken.store.users.entities.Role;
import com.ken.store.users.exceptions.EmailAlreadyRegisteredException;
import com.ken.store.users.exceptions.UserNotFoundException;
import com.ken.store.users.mappers.UserMapper;
import com.ken.store.users.repositories.UserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public List<UserDto> getAllUsers(String sortBy) {
        if (!Set.of("name", "email").contains(sortBy)) {
            // Default: sort by name
            sortBy = "name";
        }

        return userRepository.findAll(Sort.by(sortBy).ascending()).stream() // List to Stream
                .map(userMapper::toDto).toList(); // Stream to List
    }

    public UserDto getUser(Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            // return ResponseEntity.notFound().build();
            throw new UserNotFoundException(); 
        }
        return userMapper.toDto(user);
    }

    public UserDto registerUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyRegisteredException();
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto updateUser(Long id, UpdateUserRequest request) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!user.equals(authService.getCurrentUser())) {
            throw new AccessDeniedException("Access denied.");
        }

        userMapper.update(request, user);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public void deleteUser(Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!user.equals(authService.getCurrentUser())) {
            throw new AccessDeniedException("Access denied.");
        }

        userRepository.delete(user);
    }

    public void changePassword(Long id, ChangePasswordRequest request) {
        // Password can be changed without logging in!
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new UserNotFoundException();
        }

        if (passwordEncoder.matches(user.getPassword(), request.getOldPassword())) {
            throw new AccessDeniedException("Wrong password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
