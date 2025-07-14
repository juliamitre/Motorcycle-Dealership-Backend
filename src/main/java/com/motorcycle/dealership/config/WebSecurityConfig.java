package com.motorcycle.dealership.config;

import com.motorcycle.dealership.security.JwtAuthenticationEntryPoint;
import com.motorcycle.dealership.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // Removed PUBLIC_URLS array, will use direct antMatchers for clarity and reliability

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // Explicitly permit public URLs for demo reliability
                .requestMatchers("/api/auth/**").permitAll()        // Authentication endpoints (register, login, etc.)
                .requestMatchers("/api/countries").permitAll()      // Get countries
                .requestMatchers("/api/motorcycles").permitAll()    // Get paginated motorcycles
                .requestMatchers("/v3/api-docs/**").permitAll()     // Swagger API documentation
                .requestMatchers("/swagger-ui/**").permitAll()      // Swagger UI interface
                .requestMatchers("/swagger-ui.html").permitAll()    // Specific Swagger UI HTML path (sometimes needed)
                .anyRequest().authenticated()                       // All other requests require authentication
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
