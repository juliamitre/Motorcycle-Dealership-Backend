package com.motorcycle.dealership.service;

import com.motorcycle.dealership.dto.transaction.CreatePaymentIntentRequest;
import com.motorcycle.dealership.dto.transaction.CreatePaymentIntentResponse;
import com.motorcycle.dealership.entity.Transaction;
import com.motorcycle.dealership.entity.User;
import com.motorcycle.dealership.exception.CustomException;
import com.motorcycle.dealership.repository.TransactionRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final InvoiceService invoiceService;
    private final EmailService emailService;

    @Value("${stripe.api.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Transactional
    public CreatePaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request, User user) {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(request.amount())
            .setCurrency(request.currency())
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
            )
            .build();
        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Create a local transaction record to track the payment
            Transaction transaction = Transaction.builder()
                .paymentIntentId(paymentIntent.getId())
                .amount(request.amount())
                .currency(request.currency())
                .status(paymentIntent.getStatus())
                .user(user)
                .build();
            transactionRepository.save(transaction);

            log.info("Created PaymentIntent {} and local transaction record for user {}", paymentIntent.getId(), user.getEmail());

            return new CreatePaymentIntentResponse(paymentIntent.getClientSecret());
        } catch (StripeException e) {
            log.error("Stripe error while creating payment intent: {}", e.getMessage());
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating payment intent: " + e.getMessage());
        }
    }

    @Transactional
    public void handleStripeWebhook(String payload, String sigHeader) {
        if (sigHeader == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Missing Stripe-Signature header");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Webhook signature verification failed.", e);
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid webhook signature.");
        } catch (Exception e) {
            log.error("Error parsing webhook event.", e);
            throw new CustomException(HttpStatus.BAD_REQUEST, "Error parsing webhook event.");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) event.getData().getObject();
            log.info("Webhook received for successful PaymentIntent: {}", paymentIntent.getId());

            Transaction transaction = transactionRepository.findByPaymentIntentId(paymentIntent.getId())
                .orElseThrow(() -> {
                    log.error("FATAL: Received successful payment webhook for an unknown transaction. PaymentIntent ID: {}", paymentIntent.getId());
                    return new CustomException(HttpStatus.NOT_FOUND, "Transaction not found for payment intent");
                });

            // Update local transaction status
            transaction.setStatus(paymentIntent.getStatus());
            Transaction savedTransaction = transactionRepository.save(transaction);

            // Generate and email invoice
            User user = savedTransaction.getUser();
            File invoice = null;
            try {
                invoice = invoiceService.generateInvoice(user, savedTransaction);
                String emailHtml = "<h3>Thank you for your payment!</h3><p>Please find your invoice attached.</p>";
                emailService.sendEmail(user.getEmail(), "Your MotorcycleXpert Invoice", emailHtml, invoice);
            } finally {
                // Clean up the temporary invoice file after sending
                if (invoice != null && invoice.exists()) {
                    if (invoice.delete()) {
                        log.info("Successfully deleted temporary invoice file: {}", invoice.getName());
                    } else {
                        log.warn("Failed to delete temporary invoice file: {}", invoice.getName());
                    }
                }
            }
        } else {
            log.warn("Received unhandled webhook event type: {}", event.getType());
        }
    }
}
