package edu.istad.jomnorncode.controller;

import edu.istad.jomnorncode.dto.FileUploadResponse;
import edu.istad.jomnorncode.entity.FileUpload;
import edu.istad.jomnorncode.repository.FileUploadRepository;
import edu.istad.jomnorncode.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "File upload and management APIs")
public class FileController {

    private final FileUploadService fileUploadService;
    private final FileUploadRepository fileUploadRepository;

    // ────────────────────────────────────────────────
    //  General file upload (flexible – used by other services)
    // ────────────────────────────────────────────────
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload any file",
            description = "Upload file with category and optional related IDs"
    )
    @ApiResponse(responseCode = "201", description = "File uploaded successfully",
            content = @Content(schema = @Schema(implementation = FileUploadResponse.class)))
    @PreAuthorize("isAuthenticated()")  // or more specific roles
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam FileUpload.FileCategory category,
            @RequestParam(required = false) Long relatedCourseId,
            @RequestParam(required = false) Long relatedUserId) {

        FileUploadResponse response = fileUploadService.uploadFile(file, category, relatedCourseId, relatedUserId);
        return ResponseEntity.status(201).body(response);
    }

    // ────────────────────────────────────────────────
    //  Convenience endpoints for common use cases
    // ────────────────────────────────────────────────

    @PostMapping(value = "/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload profile picture for current user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FileUploadResponse> uploadProfilePicture(
            @RequestPart("file") MultipartFile file) {

        // We pass current user ID – but service will use authenticated user anyway
        // You can remove relatedUserId if service always uses current user
        FileUploadResponse response = fileUploadService.uploadProfilePicture(file, null);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/course-image/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload course thumbnail/banner")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public ResponseEntity<FileUploadResponse> uploadCourseImage(
            @PathVariable Long courseId,
            @RequestPart("file") MultipartFile file) {

        FileUploadResponse response = fileUploadService.uploadCourseImage(file, courseId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/course-material/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload course material (pdf, doc, zip, etc.)")
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    public ResponseEntity<FileUploadResponse> uploadCourseMaterial(
            @PathVariable Long courseId,
            @RequestPart("file") MultipartFile file) {

        FileUploadResponse response = fileUploadService.uploadCourseMaterial(file, courseId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/preview/{fileId}")
    @Operation(summary = "Get file preview (for images)")
    public ResponseEntity<byte[]> previewFile(@PathVariable Long fileId) {
        byte[] bytes = fileUploadService.downloadFile(fileId);

        FileUpload file = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found"));

        // Optional: only allow images for preview
        if (!file.getMimeType().startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preview not supported for this file type");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .body(bytes);
    }
}