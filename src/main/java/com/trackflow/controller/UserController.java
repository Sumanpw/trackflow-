package com.trackflow.controller;

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
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Create User - Only ADMIN - CLEARS CACHE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"users", "userById"}, allEntries = true)
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CUSTOMER");
        }
        User savedUser = userRepository.save(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // 2. Get All Users - Only ADMIN - CACHED
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Cacheable(value = "users")
    public List<User> getAllUsers() {
        System.out.println("⚡ Fetching ALL users from DATABASE");
        return userRepository.findAll();
    }

    // 3. Get User by ID - CACHED
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @Cacheable(value = "userById", key = "#id")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        System.out.println("⚡ Fetching user " + id + " from DATABASE");
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Get User by Username - Only ADMIN
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. Update User - CLEARS CACHE
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @CacheEvict(value = {"users", "userById"}, allEntries = true)
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));

        user.setFullName(userDetails.getFullName());
        user.setPhone(userDetails.getPhone());
        user.setEmail(userDetails.getEmail());

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    // 6. Delete User - Only ADMIN - CLEARS CACHE
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

    // 7. Make User Admin - Only ADMIN
    @PutMapping("/{id}/make-admin")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {"users", "userById"}, allEntries = true)
    public ResponseEntity<User> makeAdmin(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));
        user.setRole("ADMIN");
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }
}