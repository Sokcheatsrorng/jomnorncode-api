package edu.istad.jomnorncode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allowed origins – replace * with your actual frontend URLs in production
        // For development: allow localhost React/Vite/etc.
        List<String> allowedOrigins = Arrays.asList(
                "http://localhost:3000",      // Create React App
                "http://localhost:5173",      // Vite (very common)
                "http://localhost:4200",      // Angular (if you use it)
                "https://your-frontend-domain.com",   // ← add your production domain later
                "https://www.your-frontend-domain.com"
        );

        configuration.setAllowedOrigins(allowedOrigins);

        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allowed headers (very permissive – safe for JWT/Auth)
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Expose headers your frontend might need (e.g. Authorization for JWT)
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));

        // Allow credentials? → Only if you use cookies/sessions (not recommended for JWT)
        // Keep false if using Bearer token
        configuration.setAllowCredentials(false);

        // Cache preflight (OPTIONS) response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}