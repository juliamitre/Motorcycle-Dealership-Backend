package com.motorcycle.dealership.controller;

import com.motorcycle.dealership.dto.auth.*;
import com.motorcycle.dealership.dto.transaction.UserResponse;
import com.motorcycle.dealership.entity.User;
import com.motorcycle.dealership.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user registration, login, and password management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and sends an OTP for verification.")
    @ApiResponse(responseCode = "201", description = "User registration initiated. Please check email for OTP.")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify user's OTP", description = "Verifies the OTP and sends a password creation link if successful.")
    public ResponseEntity<Void> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Set or Reset user's password", description = "Sets the user's password using a valid token.")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Authenticates a user and returns access and refresh tokens.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user details", description = "Retrieves the details of the currently authenticated user.")
    public ResponseEntity<UserResponse> getLoggedInUser(@AuthenticationPrincipal User user) {
        // The @AuthenticationPrincipal annotation magically injects the currently logged-in User object
        return ResponseEntity.ok(UserResponse.fromUser(user));
    }
}
