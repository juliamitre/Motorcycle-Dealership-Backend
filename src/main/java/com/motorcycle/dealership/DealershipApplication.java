package com.motorcycle.dealership;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // This is crucial for the @Async EmailService to work
public class DealershipApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealershipApplication.class, args);
    }

}
