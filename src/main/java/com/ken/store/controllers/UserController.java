package com.ken.store.controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.ken.store.dtos.ChangePasswordRequest;
import com.ken.store.dtos.RegisterUserRequest;
import com.ken.store.dtos.UpdateUserRequest;
import com.ken.store.dtos.UserDto;
import com.ken.store.mappers.UserMapper;
import com.ken.store.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // @RequestMapping's default method is GET,
    // so, it is same as @GetMapping
    // But RequestMapping can use other mthods like POST, DELETE...
    // @RequestMapping("") = @GetMapping("")
    @Operation(summary = "Get all users")
    @GetMapping("")
    public List<UserDto> getAllUsers(
            // required = false: sort is optional
            // if sort is not provided, it will be defaultValue
            // by default, defaultValue = null
            // name = "sort": make sure the url is "sort"
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy) {
        if (!Set.of("name", "email").contains(sortBy)) {
            // Default: sort by name
            sortBy = "name";
        }

        return userRepository.findAll(Sort.by(sortBy).ascending()).stream() // List to Stream
                .map(userMapper::toDto).toList(); // Stream to List
    }

    @Operation(summary = "Get a use by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return ResponseEntity.notFound().build();
        }

        // return new ResponseEntity<>(user, HttpStatus.OK);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @Operation(summary = "Register a user")
    @PostMapping
    // ResponseEntity<?> it can return ResponseEntity<UserDto>
    // and ResponseEntity<Map<String, String>>
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(
                Map.of("email", "Email has been registered")
            );
        }
        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        var userDto = userMapper.toDto(user);

        // Create URI (Uniform Resource Identifier)
        var uri = uriBuilder.path("/users/{id}")
                .buildAndExpand(user.getId()).toUri();

        return ResponseEntity.created(uri).body(userDto);
    }

    @Operation(summary = "Update user's information")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userMapper.update(request, user);
        userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change user's password")
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getPassword().equals(request.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

    /* // We will use global validation handler  
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException exception) {
        var errors = new HashMap<String, String>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    } */

}
