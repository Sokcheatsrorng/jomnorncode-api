package edu.istad.jomnorncode.controller;

import edu.istad.jomnorncode.dto.UserMeResponse;
import edu.istad.jomnorncode.dto.UserRequest;
import edu.istad.jomnorncode.dto.UserResponse;
import edu.istad.jomnorncode.entity.User;
import edu.istad.jomnorncode.repository.UserRepository;
import edu.istad.jomnorncode.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User Management APIs")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user", description = "Only ADMIN can create users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> getMe() {
        // Get email from JWT (this is what is stored in "sub")
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)   // ← CHANGE THIS LINE
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(UserMeResponse.fromEntity(user));
    }
    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    // 1. Get all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users with pagination", description = "Only ADMIN can view all users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(required = false, defaultValue = "false") boolean all,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",                  // ← real field, change if needed
                    direction = Sort.Direction.DESC      // newest first
            )
            Pageable pageable) {
        Page<UserResponse> response = userService.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    // 2. Search users
    @GetMapping("/search/{searchTerm}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search users by username, email, or name")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @PathVariable String searchTerm,
            @RequestParam(required = false, defaultValue = "false") boolean all,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {
        Page<UserResponse> response = userService.searchUsers(searchTerm, pageable);
        return ResponseEntity.ok(response);
    }

    // 3. Users by role
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by role")
    public ResponseEntity<Page<UserResponse>> getUsersByRole(
            @PathVariable String roleName,
            @RequestParam(required = false, defaultValue = "false") boolean all,
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {
        Page<UserResponse> response = userService.getUsersByRole(roleName, pageable);
        return ResponseEntity.ok(response);
    }
}
