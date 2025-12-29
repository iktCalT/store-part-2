package com.ken.store.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ken.store.dtos.UserDto;
import com.ken.store.mappers.UserMapper;
import com.ken.store.repositories.UserRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // @RequestMapping's default method is GET,
    // so, it is same as @GetMapping
    // But RequestMapping can use other mthods like POST, DELETE...
    // @RequestMapping("") = @GetMapping("")
    @GetMapping("")
    public List<UserDto> fetchAllUsers() {
        return userRepository.findAll()
                .stream() // List to Stream
                .map(userMapper::toDto)
                .toList(); // Stream to List
    }

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
}
