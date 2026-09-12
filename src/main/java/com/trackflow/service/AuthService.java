package com.trackflow.service;

import com.trackflow.dto.AuthResponse;
import com.trackflow.dto.LoginRequest;
import com.trackflow.dto.RegisterRequest;
import com.trackflow.entity.User;
import com.trackflow.exception.CustomException;
import com.trackflow.repository.UserRepository;
import com.trackflow.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setActive(true);
        user.setRole("CUSTOMER");

        userRepository.save(user);

        String accessToken = jwtUtils.generateToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        return new AuthResponse(accessToken, refreshToken, "Registration successful");
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new CustomException("User not found"));
            String accessToken = jwtUtils.generateToken(user.getUsername(), user.getRole());
            String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());
            return new AuthResponse(accessToken, refreshToken, "Login successful");
        }
        throw new CustomException("Invalid credentials");
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new CustomException("Invalid or expired refresh token");
        }

        String username = jwtUtils.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found"));

        String newAccessToken = jwtUtils.generateToken(user.getUsername(), user.getRole());

        return new AuthResponse(newAccessToken, refreshToken, "Token refreshed successfully");
    }
}