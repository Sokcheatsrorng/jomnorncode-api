package edu.istad.jomnorncode.dto;


import edu.istad.jomnorncode.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
public class UserMeResponse {
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String profilePicture;
    private String bio;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Set<String> roles;   // only role names

    public static UserMeResponse fromEntity(User user) {
        return UserMeResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFirstName()+" "+user.getLastName())
                .bio(user.getBio())
                .isActive(user.getIsActive())
                .profilePicture(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream()
                        .map(role -> role.getRoleName())
                        .collect(java.util.stream.Collectors.toSet()))
                .build();
    }
}