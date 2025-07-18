package com.motorcycle.dealership.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,

    @NotBlank(message = "Password is required")
    String password,

    @NotBlank(message = "Recaptcha response is required")
    String recaptcha
) {}
