package com.motorcycle.dealership.util;

import java.security.SecureRandom;

public final class OtpUtil {

    private OtpUtil() {}

    public static String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(AppConstants.OTP_LENGTH);
        for (int i = 0; i < AppConstants.OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
