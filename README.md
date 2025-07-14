# Motorcycle Dealership Backend

This is the backend for the **MotorcycleXpert** web application — an e-commerce-style platform for browsing and purchasing motorcycles. The backend is built with **Spring Boot**, uses **JWT for authentication**, integrates with **Stripe for payments**, generates **PDF invoices**, and sends them via **email**.

---
## 🚀 Features

- ✅ User Authentication (Sign Up, OTP Verification, Login, Password Reset)
- 🔐 JWT-based Authorization (Access & Refresh Tokens)
- 📬 Email Notifications (Gmail via SMTP)
- 🏍️ Static Motorcycle Catalog (Paginated & Searchable)
- 💳 Stripe Payment Integration (Payment Intent + Webhooks)
- 🧾 PDF Invoice Generation with iText
- 🤖 Google ReCAPTCHA Integration (bypassed in dev)
- 🧪 Postman Collection for API Testing (Import + Run Scenarios)

---

## 🧠 Tech Stack

| Layer        | Technology               |
|--------------|--------------------------|
| Backend      | Spring Boot 3.2.5        |
| Database     | MySQL                    |
| Auth         | JWT (jjwt)               |
| Payments     | Stripe API               |
| PDF Export   | iText 8.0.4              |
| Email        | JavaMailSender (SMTP)    |
| Docs         | Swagger / OpenAPI (SpringDoc) |

---

## 📦 Project Structure (Simplified)

```plaintext
├── controller/          # API Endpoints (Auth, Transaction, etc.)
├── service/             # Core Business Logic
├── entity/              # JPA Entities
├── dto/                 # Request/Response Models
├── config/              # JWT, CORS, Security Configs
├── exception/           # Custom Exception Handling
├── util/                # Helpers (OTP, Recaptcha, etc.)
├── repository/          # JPA Repositories
└── resources/
    └── application.properties
⚙️ Getting Started
✅ Prerequisites
Java 17

Maven

MySQL

Stripe Account

Gmail Account (App Password enabled)

Postman (for API testing)

🧪 Running the App
Step 1: MySQL Setup

CREATE DATABASE dealership_db;
Or let Hibernate do it (already configured via ddl-auto=update)

Step 2: Run Backend
bash
Copy
Edit
mvn spring-boot:run
App will be available at:
📍 http://localhost:8080

📬 Postman API Testing
Import the provided Postman Collection (see /postman/ folder or export from your own workspace).

Setup:
Set up an Environment in Postman with variables like:

base_url → http://localhost:8080

access_token, refresh_token, user_id, etc.

Run requests in this order:

✅ Register

🔄 Verify OTP

🔑 Login → copy JWT to access_token

🛒 Create Payment Intent

💳 Trigger Webhook (optional)

📧 Check your email for the PDF invoice

Collection Runner:
You can use Collection Runner to run automated test flows, like:

Invalid login

Invalid OTP

Successful Payment → Invoice sent

Missing Bearer Token → Unauthorized

📚 API Documentation (Swagger)
Once app is running, open:
📍 http://localhost:8080/swagger-ui.html
Or:
📍 /v3/api-docs

🚧 Notes
ReCAPTCHA is bypassed in RecaptchaUtil.java during local development

Stripe webhook secret can be a dummy for testing, real one needed for production

Use Gmail App Password — not your regular password

✅ To-Do / Improvements
 Add bike filtering/search/sorting

 Switch to production Stripe & Gmail

 Add frontend (Angular or React)

 Deploy (Render, Heroku, Fly.io, etc.)

👨‍💻 Author
Built by Dona Juana
# Motorcycle Dealership Backend

This is the backend for the Motorcycle Dealership application, built with Spring Boot.

## Features

*   User Authentication (Registration, OTP Verification, Password Reset, Login)
*   JWT-based Authorization
*   Static Content APIs (Countries, Paginated Motorcycles)
*   Stripe Payment Integration (Payment Intent creation, Webhook handling for invoices)
*   Email Service for OTPs, Password Resets, and Invoices
*   ReCAPTCHA Integration for bot protection

## Getting Started

### Prerequisites

*   Java 17
*   Maven
*   MySQL Database (configured in `src/main/resources/application.properties`)
*   Stripe Account (for API keys and testing payments)
*   Gmail Account (or other SMTP service for email sending)

### Configuration

Before running the application, update the `src/main/resources/application.properties` file with your specific configurations:

*   **Database:** `spring.datasource.url`, `username`, `password`
*   **JWT Secret Key:** `app.jwt.secret-key` (Use a strong, random string)
*   **Stripe Keys:** `stripe.api.secret-key` (from your Stripe Dashboard), `stripe.webhook.secret` (can be a dummy like `whsec_dummy_for_testing` for local testing)
*   **ReCAPTCHA Secret Key:** `recaptcha.secret-key` (For local testing, the `RecaptchaUtil` is bypassed. For production, you'd need a real key.)
*   **Email:** `spring.mail.username`, `spring.mail.password` (Use a Gmail **App Password** if using Gmail)
*   **Frontend URL:** `app.frontend-url` (e.g., `http://localhost:5173`)

### Running the Application

You can run the application using Maven:

```bash
mvn spring-boot:run
