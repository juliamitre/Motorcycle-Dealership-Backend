package com.motorcycle.dealership.dto.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePaymentIntentRequest(
    @NotNull(message = "Amount is required")
    @Min(value = 50, message = "Amount must be at least 50 cents")
    Long amount,

    @NotBlank(message = "Currency is required")
    String currency
) {}
