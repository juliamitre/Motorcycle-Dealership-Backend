package com.motorcycle.dealership.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.motorcycle.dealership.entity.Transaction;
import com.motorcycle.dealership.entity.User;
import com.motorcycle.dealership.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException; // <-- IMPORTANT IMPORT
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;

@Service
@Slf4j
public class InvoiceService {

    /**
     * Generates a temporary PDF invoice file.
     * The caller is responsible for deleting the file after use.
     * @param user The user for whom the invoice is generated.
     * @param transaction The transaction details.
     * @return A temporary File object representing the PDF.
     */
    public File generateInvoice(User user, Transaction transaction) {
        File invoiceFile;

        // --- WRAP ENTIRE LOGIC IN TRY-CATCH FOR IOEXCEPTION ---
        try {
            // Create a temporary file to avoid cluttering the project directory
            invoiceFile = Files.createTempFile("invoice-" + transaction.getId() + "-", ".pdf").toFile();
            log.info("Generating invoice at temporary path: {}", invoiceFile.getAbsolutePath());

            // Using try-with-resources automatically closes the resources
            try (PdfWriter writer = new PdfWriter(invoiceFile);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                // --- Invoice Header ---
                document.add(new Paragraph("MotorcycleXpert Invoice")
                    .setBold()
                    .setFontSize(22)
                    .setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("Invoice ID: " + transaction.getId())
                    .setTextAlignment(TextAlignment.RIGHT));
                document.add(new Paragraph("Date: " + transaction.getCreatedAt().toLocalDate())
                    .setTextAlignment(TextAlignment.RIGHT));

                document.add(new Paragraph("\n"));

                // --- Billing Information ---
                document.add(new Paragraph("Bill To:").setBold());
                document.add(new Paragraph(user.getFirstName() + " " + user.getLastName()));
                document.add(new Paragraph(user.getEmail()));

                document.add(new Paragraph("\n\n"));

                // --- Payment Details ---
                document.add(new Paragraph("Payment Details:").setBold());

                // Convert amount from cents to dollars for display
                BigDecimal amountInCurrencyUnit = new BigDecimal(transaction.getAmount())
                    .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                document.add(new Paragraph("Amount: " + amountInCurrencyUnit + " " + transaction.getCurrency().toUpperCase()));
                document.add(new Paragraph("Status: " + transaction.getStatus()));
                document.add(new Paragraph("Payment Intent ID: " + transaction.getPaymentIntentId()));

            } // The 'close()' methods that can throw IOException are called here implicitly

            log.info("Invoice generated successfully for transaction ID: {}", transaction.getId());
            return invoiceFile;

        } catch (IOException e) { // <-- This block now handles the checked exception
            log.error("An I/O error occurred during invoice generation: {}", e.getMessage());
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate invoice due to a server file error.");
        }
    }
}
