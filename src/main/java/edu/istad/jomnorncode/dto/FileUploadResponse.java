package edu.istad.jomnorncode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.istad.jomnorncode.entity.FileUpload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponse {

    @JsonProperty("file_id")
    private Long id;

    @JsonProperty("original_file_name")
    private String originalFileName;

    @JsonProperty("stored_file_name")
    private String storedFileName;

    @JsonProperty("file_path")
    private String filePath;

    @JsonProperty("file_type")
    private String fileType;

    @JsonProperty("file_size")
    private Long fileSize;

    @JsonProperty("mime_type")
    private String mimeType;

    @JsonProperty("category")
    private String category;

    @JsonProperty("uploaded_by_id")
    private Long uploadedById;

    @JsonProperty("uploaded_by_name")
    private String uploadedByName;

    @JsonProperty("related_course_id")
    private Long relatedCourseId;

    @JsonProperty("related_user_id")
    private Long relatedUserId;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("download_url")
    private String downloadUrl;


    public static FileUploadResponse fromEntity(FileUpload entity, String downloadUrl) {
        return FileUploadResponse.builder()
                .id(entity.getId())
                .originalFileName(entity.getOriginalFileName())
                .storedFileName(entity.getStoredFileName())
                .filePath(entity.getFilePath())
                .fileType(entity.getFileType())
                .fileSize(entity.getFileSize())
                .mimeType(entity.getMimeType())
                .category(entity.getCategory().name())
                .uploadedById(entity.getUploadedBy() != null ? entity.getUploadedBy().getUserId() : null)
                .uploadedByName(entity.getUploadedBy() != null ? entity.getUploadedBy().getUsername() : null)
                .relatedCourseId(entity.getRelatedCourseId())
                .relatedUserId(entity.getRelatedUserId())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .downloadUrl(downloadUrl)
                .build();
    }
}
