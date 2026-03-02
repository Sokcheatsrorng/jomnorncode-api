package edu.istad.jomnorncode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;

    private Long userId;

    private Long courseId;

    private LocalDateTime enrollmentDate;

    private LocalDateTime completionDate;

    private Integer progressPercentage;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
