package com.motorcycle.dealership.dto.error;

import java.time.LocalDateTime;

public record ErrorResponse(
    int statusCode,
    String message,
    LocalDateTime timestamp
) {}
