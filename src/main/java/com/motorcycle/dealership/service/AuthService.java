package com.motorcycle.dealership.service;

import com.motorcycle.dealership.dto.auth.*;
import com.motorcycle.dealership.entity.Role;
import com.motorcycle.dealership.entity.User;
import com.motorcycle.dealership.exception.CustomException;
import com.motorcycle.dealership.repository.UserRepository;
import com.motorcycle.dealership.security.JwtTokenProvider;
import com.motorcycle.dealership.util.AppConstants;
import com.motorcycle.dealership.util.OtpUtil;
import com.motorcycle.dealership.util.RecaptchaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RecaptchaUtil recaptchaUtil;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Transactional
    public void register(RegisterRequest request) {
        if (!recaptchaUtil.verifyRecaptcha(request.recaptcha())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid ReCAPTCHA");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        String otp = OtpUtil.generateOtp();
        User user = User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email())
            .country(request.country())
            .role(Role.USER) // Default role
            .otp(otp)
            .otpExpiryTime(LocalDateTime.now().plusMinutes(AppConstants.OTP_EXPIRATION_MINUTES))
            .activated(false) // Not activated until password is set
            .build();
        userRepository.save(user);

        // Send OTP email
        String emailHtml = "<h3>Your OTP for MotorcycleXpert is: " + otp + "</h3>";
        emailService.sendEmail(request.email(), "Verify Your Account - OTP", emailHtml, null);
    }

    @Transactional
    public void verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getOtp() == null || !user.getOtp().equals(request.otp())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
        if (user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "OTP has expired");
        }

        // OTP is valid, generate password reset token
        String token = UUID.randomUUID().toString();
        user.setOtp(null); // Clear OTP
        user.setOtpExpiryTime(null);
        user.setResetPasswordToken(token);
        user.setResetPasswordExpiryTime(LocalDateTime.now().plusHours(AppConstants.RESET_TOKEN_EXPIRATION_HOURS));
        userRepository.save(user);

        // Send email with password creation link
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        String emailHtml = "<h3>Click the link to set your password:</h3><a href=\"" + resetLink + "\">Set Password</a>";
        emailService.sendEmail(request.email(), "Set Your Password", emailHtml, null);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.token())
            .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid or expired token"));

        if (user.getResetPasswordExpiryTime().isBefore(LocalDateTime.now())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiryTime(null);
        user.setActivated(true); // User is now fully active
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        if (!recaptchaUtil.verifyRecaptcha(request.recaptcha())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid ReCAPTCHA");
        }

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email()).orElseThrow();
        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }
}
