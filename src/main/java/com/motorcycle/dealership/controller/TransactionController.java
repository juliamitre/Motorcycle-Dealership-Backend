package com.motorcycle.dealership.controller;

import com.motorcycle.dealership.dto.transaction.CreatePaymentIntentRequest;
import com.motorcycle.dealership.dto.transaction.CreatePaymentIntentResponse;
import com.motorcycle.dealership.entity.User;
import com.motorcycle.dealership.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "APIs for handling payments with Stripe")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/create-payment-intent")
    @Operation(summary = "Create a Stripe Payment Intent", description = "Initializes a payment with Stripe and returns a client secret.")
    public ResponseEntity<CreatePaymentIntentResponse> createPaymentIntent(
        @Valid @RequestBody CreatePaymentIntentRequest request,
        @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(transactionService.createPaymentIntent(request, user));
    }

    @PostMapping("/webhook")
    @Operation(summary = "Stripe Webhook Handler", description = "Handles incoming events from Stripe to confirm payments. This endpoint should not be called directly by the frontend.")
    public ResponseEntity<Void> stripeWebhook(
        @RequestBody String payload,
        @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        transactionService.handleStripeWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }
}
