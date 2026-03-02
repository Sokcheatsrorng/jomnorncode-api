package edu.istad.jomnorncode.config;

import edu.istad.jomnorncode.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
                        // 1. Completely public - no auth needed
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/courses/public/**").permitAll()

                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        .requestMatchers(HttpMethod.GET,"/files/preview/**").permitAll()

                        // 2. Authenticated endpoints - USER or ADMIN can access
                        //    (order matters → put more permissive first)
                        .requestMatchers(HttpMethod.GET, "/courses/**").authenticated()      // any authenticated user sees course details
                        .requestMatchers(HttpMethod.GET, "/users/**").authenticated()        // profile, own data, etc.

                        // 3. Write operations - only ADMIN
                        .requestMatchers(HttpMethod.POST, "/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/courses/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/lessons/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/lessons/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/lessons/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/quizzes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/quizzes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/quizzes/**").hasRole("ADMIN")

                        // 4. User-specific actions (enroll, review, etc.)
                        .requestMatchers(HttpMethod.POST, "/enrollments/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/reviews/**").hasRole("USER")

                        // 5. Catch-all: everything else requires authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}