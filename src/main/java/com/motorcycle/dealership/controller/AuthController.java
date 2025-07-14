package com.motorcycle.dealership.controller;

import com.motorcycle.dealership.dto.auth.*; // Assuming UserResponse is in here now
import com.motorcycle.dealership.entity.User;
import com.motorcycle.dealership.exception.CustomException; // <--- ADDED IMPORT FOR CustomException
import com.motorcycle.dealership.repository.UserRepository; // <--- ADDED IMPORT FOR UserRepository
import com.motorcycle.dealership.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; // <--- ADDED IMPORT FOR HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails; // <--- ADDED IMPORT FOR UserDetails
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
// Renamed the tag for better sorting in Swagger UI
@Tag(name = "1. Authentication", description = "APIs for user registration, login, and password management")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository; // <--- ADDED: Injected UserRepository

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account and sends an OTP for verification.")
    // Added detailed responses
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registration initiated. Please check email for OTP."),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists", content = @Content)
    })
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify user's OTP", description = "Verifies the OTP and enables the user to set a password.")
    // Added detailed responses
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OTP verified successfully. User can now set password."),
        @ApiResponse(responseCode = "400", description = "Invalid or expired OTP", content = @Content)
    })
    public ResponseEntity<Void> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Set or Reset user's password", description = "Sets the user's password using a valid token from OTP verification.")
    // Added detailed responses
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Password has been set/reset successfully."),
        @ApiResponse(responseCode = "400", description = "Invalid token or password", content = @Content)
    })
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user", description = "Authenticates a user and returns access and refresh tokens.")
    // Added detailed responses, including the schema for the success response
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials or account not activated", content = @Content)
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user details", description = "Retrieves the details of the currently authenticated user.")
    @SecurityRequirement(name = "bearerAuth") // <-- This is the crucial part for protected endpoints
    // Added detailed responses
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User details retrieved",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - No token or invalid token provided", content = @Content)
    })
    // Modified: Changed parameter type to UserDetails and explicitly fetch User entity
    public ResponseEntity<UserResponse> getLoggedInUser(@AuthenticationPrincipal UserDetails userDetails) {
        // Retrieve the full User entity from the database using the email (username)
        // Spring Security provides UserDetails, but we need our custom User entity for full data.
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Logged in user not found in database"));

        return ResponseEntity.ok(UserResponse.fromUser(user));
    }
}
