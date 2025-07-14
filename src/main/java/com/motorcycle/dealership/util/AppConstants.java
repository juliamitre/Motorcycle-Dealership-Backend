package com.motorcycle.dealership.util;

public final class AppConstants {
    // Private constructor to prevent instantiation
    private AppConstants() {}

    public static final int OTP_LENGTH = 6;
    public static final int OTP_EXPIRATION_MINUTES = 70;
    // RESET_TOKEN_EXPIRATION_MINUTES is no longer used because
    // reset tokens are set to never expire for demo purposes in AuthService.java
}
