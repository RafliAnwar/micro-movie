package dev.dzul.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/movies/**", "/api/subs/**", "/public/**").permitAll() // Public endpoints
                        .anyRequest().permitAll() // All other endpoints require authentication
                ); // Basic auth for other endpoints (optional)

        return http.build();
    }
}
