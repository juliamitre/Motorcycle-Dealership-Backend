package com.motorcycle.dealership.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class RecaptchaUtil {

    @Value("${recaptcha.secret-key}")
    private String recaptchaSecret;

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verifyRecaptcha(String recaptchaResponse) {
        // --- TEMPORARY BYPASS FOR LOCAL DEVELOPMENT/PRESENTATION ---
        // For a real application, you MUST remove this line!
        log.warn("ReCAPTCHA verification bypassed for development purposes.");
        return true;
        // -------------------------------------------------------------

        // The original code (uncomment this for production):
        /*
        if (recaptchaResponse == null || recaptchaResponse.isBlank()) {
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", recaptchaSecret);
        requestMap.add("response", recaptchaResponse);

        try {
            RecaptchaApiResponse response = restTemplate.postForObject(RECAPTCHA_VERIFY_URL, requestMap, RecaptchaApiResponse.class);
            if (response != null && response.isSuccess()) {
                log.info("ReCAPTCHA verification successful for hostname: {}", response.getHostname());
                return true;
            } else {
                log.warn("ReCAPTCHA verification failed. Response: {}", response);
                return false;
            }
        } catch (Exception e) {
            log.error("Error during ReCAPTCHA verification request", e);
            return false;
        }
        */
    }

    // Private inner class to map the JSON response from Google
    @Data
    private static class RecaptchaApiResponse {
        private boolean success;
        private String challenge_ts;
        private String hostname;
        private List<String> errorCodes;
    }
}
