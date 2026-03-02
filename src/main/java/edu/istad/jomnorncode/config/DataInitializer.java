package edu.istad.jomnorncode.config;

import edu.istad.jomnorncode.entity.Role;
import edu.istad.jomnorncode.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default roles
        initializeRoles();
    }

    private void initializeRoles() {
        // Create ADMIN role if it doesn't exist
        if (!roleRepository.findByRoleName("ADMIN").isPresent()) {
            Role adminRole = Role.builder()
                    .roleName("ADMIN")
                    .description("Administrator role with full access")
                    .build();
            roleRepository.save(adminRole);
            log.info("ADMIN role created successfully");
        }

        // Create USER role if it doesn't exist
        if (!roleRepository.findByRoleName("USER").isPresent()) {
            Role userRole = Role.builder()
                    .roleName("USER")
                    .description("Regular user role with limited access")
                    .build();
            roleRepository.save(userRole);
            log.info("USER role created successfully");
        }

        // Create INSTRUCTOR role if it doesn't exist
        if (!roleRepository.findByRoleName("INSTRUCTOR").isPresent()) {
            Role instructorRole = Role.builder()
                    .roleName("INSTRUCTOR")
                    .description("Instructor role for course creation and management")
                    .build();
            roleRepository.save(instructorRole);
            log.info("INSTRUCTOR role created successfully");
        }
    }
}
