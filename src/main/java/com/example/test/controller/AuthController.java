package com.example.test.controller;

import com.example.test.dto.AuthResponse;
import com.example.test.dto.LoginRequest;
import com.example.test.dto.RegisterRequest;
import com.example.test.entity.User;
import com.example.test.service.UserService;
import com.example.test.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            // Validate input
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, null, "Username is required"));
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, null, "Email is required"));
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, null, "Password is required"));
            }
            if (request.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, null, "Password must be at least 6 characters"));
            }
            if (!request.getPassword().matches(".*[a-zA-Z].*")) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, null, "Password must contain at least one letter"));
            }

            // Register user
            User user = userService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword()
            );

            // Generate token
            String token = jwtUtil.generateToken(user.getUsername());

            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), "Registration successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, null, "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            // Validate input
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, null, "Username is required"));
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, null, "Password is required"));
            }

            // Find user
            User user = userService.findByUsername(request.getUsername());

            // Validate password
            if (!userService.validatePassword(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, null, "Invalid username or password"));
            }

            // Generate token
            String token = jwtUtil.generateToken(user.getUsername());

            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), "Login successful"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, "Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, null, "Login failed: " + e.getMessage()));
        }
    }
}

