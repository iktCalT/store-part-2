package com.ken.store.users.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.ken.store.common.dtos.ErrorDto;
import com.ken.store.users.dtos.ChangePasswordRequest;
import com.ken.store.users.dtos.RegisterUserRequest;
import com.ken.store.users.dtos.UpdateUserRequest;
import com.ken.store.users.dtos.UserDto;
import com.ken.store.users.exceptions.EmailAlreadyRegisteredException;
import com.ken.store.users.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    // @RequestMapping's default method is GET,
    // so, it is same as @GetMapping
    // But RequestMapping can use other mthods like POST, DELETE...
    // @RequestMapping("") = @GetMapping("")
    @Operation(summary = "Get all users")
    @GetMapping("")
        // required = false:    
        //      sort is optional
        //      if sort is not provided, it will be defaultValue
        //      by default, defaultValue = null
        // name = "sort":       
        //      make sure the url is "sort"
    public List<UserDto> getAllUsers(
            @RequestParam(required = false, defaultValue = "", name = "sort") String sortBy) {
        return userService.getAllUsers(sortBy);
    }

    @Operation(summary = "Get a use by its ID")
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @Operation(summary = "Register a user")
    @PostMapping
    // ResponseEntity<?> it can return ResponseEntity<UserDto>
    // and ResponseEntity<Map<String, String>>
    public ResponseEntity<UserDto> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder) {
        
        var userDto = userService.registerUser(request);

        // Create URI (Uniform Resource Identifier)
        var uri = uriBuilder.path("/users/{id}")
                .buildAndExpand(userDto.getId()).toUri();

        return ResponseEntity.created(uri).body(userDto);
    }

    @Operation(summary = "Update user's information")
    @PutMapping("/{id}")
    public UserDto updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change user's password")
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ErrorDto> handleEmailRegistered() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorDto("Email has been registered")
        );
    }
}
