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
public class CertificateResponse {

    private Long id;

    private Long userId;

    private String userName;

    private Long courseId;

    private String courseName;

    private String certificateNumber;

    private LocalDateTime issueDate;

    private String fileUrl;

    private LocalDateTime revokedAt;

    private String revokeReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
