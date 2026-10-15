package com.jenitaroselin.personalfinancemanagement.controller;

import com.jenitaroselin.personalfinancemanagement.dto.request.ChangePasswordRequest;
import com.jenitaroselin.personalfinancemanagement.dto.request.LoginRequest;
import com.jenitaroselin.personalfinancemanagement.dto.request.RegisterRequest;
import com.jenitaroselin.personalfinancemanagement.dto.request.UpdateProfileRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.AuthResponse;
import com.jenitaroselin.personalfinancemanagement.dto.response.UserResponse;
import com.jenitaroselin.personalfinancemanagement.service.AuthService;
import com.jenitaroselin.personalfinancemanagement.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Exposes REST endpoints for user registration, authentication, profile retrieval, and password updates.
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping({"/me", "/user"})
    public ResponseEntity<UserResponse> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(authService.getCurrentUserProfile(userId));
    }

    @PutMapping({"/me", "/user"})
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(authService.updateProfile(userId, request));
    }

    @PutMapping({"/me/password", "/user/password"})
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        authService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping({"/me", "/user"})
    public ResponseEntity<Void> deleteAccount() {
        Long userId = SecurityUtils.getCurrentUserId();
        authService.deleteAccount(userId);
        return ResponseEntity.noContent().build();
    }
}
