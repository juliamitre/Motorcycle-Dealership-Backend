package com.motorcycle.dealership.dto.auth;

public record AuthResponse(
    String accessToken,
    String refreshToken
) {}
