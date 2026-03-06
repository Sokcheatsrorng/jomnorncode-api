package edu.istad.jomnorncode.service;

import edu.istad.jomnorncode.dto.AuthRequest;
import edu.istad.jomnorncode.dto.AuthResponse;
import edu.istad.jomnorncode.dto.RegistrationRequest;
import edu.istad.jomnorncode.dto.UserMeResponse;
import edu.istad.jomnorncode.entity.Role;
import edu.istad.jomnorncode.entity.User;
import edu.istad.jomnorncode.exception.ResourceNotFoundException;
import edu.istad.jomnorncode.repository.RoleRepository;
import edu.istad.jomnorncode.repository.UserRepository;
import edu.istad.jomnorncode.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;


    @Transactional
    public AuthResponse register(RegistrationRequest request) {
        try {
            // Check duplicates
            if (userRepository.existsByEmail(request.getEmail())) {
                return AuthResponse.builder()
                        .success(false)
                        .message("User already exists with this email")
                        .build();
            }

            if (userRepository.existsByUsername(request.getUsername())) {
                return AuthResponse.builder()
                        .success(false)
                        .message("Username already exists")
                        .build();
            }

            // Get default USER role
            Role userRole = roleRepository.findByRoleName("USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Default USER role not found"));

            // Create user
            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .isActive(true)
                    .isDeleted(false)
                    .build();

            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);

            // Save user
            User savedUser = userRepository.save(user);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // === FIXED PART ===
            // Load UserDetails (this is what your JwtTokenUtil.generateToken expects)
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String token = jwtTokenUtil.generateToken(userDetails);

            log.info("User registered successfully: {}", request.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .message("User registered successfully")
                    .success(true)
                    .user(UserMeResponse.fromEntity(savedUser))
                    .build();

        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return AuthResponse.builder()
                    .success(false)
                    .message("Registration failed: " + e.getMessage())
                    .build();
        }
    }

    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Get UserDetails from Spring Security (this is what generateToken expects)
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generate token using UserDetails (this matches your JwtTokenUtil)
            String token = jwtTokenUtil.generateToken(userDetails);

            // Load full user entity for the response
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found after authentication"));

            log.info("User logged in successfully: {}", request.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .message("Login successful")
                    .success(true)
                    .user(UserMeResponse.fromEntity(user))
                    .build();

        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid email or password")
                    .build();
        }
    }
}