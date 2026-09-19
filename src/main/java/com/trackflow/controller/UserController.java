package com.trackflow.controller;

import com.trackflow.dto.UserRequest;
import com.trackflow.dto.UserResponse;
import com.trackflow.entity.User;
import com.trackflow.exception.CustomException;
import com.trackflow.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Create User - Returns UserResponse (no password!)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"users", "userById"}, allEntries = true)
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setFullName(request.getFullName());
        user.setRole(request.getRole() != null ? request.getRole() : "CUSTOMER");

        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(UserResponse.fromEntity(savedUser), HttpStatus.CREATED);
    }

    // 2. Get All Users - Returns List<UserResponse>
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "users")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 3. Get User by ID - Returns UserResponse
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @Cacheable(value = "userById", key = "#id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    // 4. Get User by Username
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found"));
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    // 5. Update User
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @CacheEvict(value = {"users", "userById"}, allEntries = true)
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

    // 6. Delete User
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"users", "userById"}, allEntries = true)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new CustomException("User not found");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // 7. Make Admin
    @PutMapping("/{id}/make-admin")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"users", "userById"}, allEntries = true)
    public ResponseEntity<UserResponse> makeAdmin(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));
        user.setRole("ADMIN");
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }
}