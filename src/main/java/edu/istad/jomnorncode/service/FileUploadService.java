package edu.istad.jomnorncode.service;

import edu.istad.jomnorncode.config.FileUploadProperties;
import edu.istad.jomnorncode.dto.FileUploadResponse;
import edu.istad.jomnorncode.entity.FileUpload;
import edu.istad.jomnorncode.entity.User;
import edu.istad.jomnorncode.repository.FileUploadRepository;
import edu.istad.jomnorncode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {

    private final FileUploadRepository fileUploadRepository;
    private final UserRepository userRepository;
    private final FileUploadProperties fileUploadProperties;

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    public FileUploadResponse uploadFile(MultipartFile file, FileUpload.FileCategory category, Long relatedCourseId, Long relatedUserId) {
        try {
            // Validate file
            validateFile(file, category);

            // Get current user
            User currentUser = getCurrentUser();

            // Create directory if not exists
            String uploadDir = fileUploadProperties.getDir();
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate unique filename
            String storedFileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));

            // Save file to disk
            Path filePath = Paths.get(uploadDir, storedFileName);
            Files.write(filePath, file.getBytes());

            // Save file metadata to database
            FileUpload fileUpload = FileUpload.builder()
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(storedFileName)
                    .filePath(filePath.toString())
                    .fileType(getFileExtension(file.getOriginalFilename()).toLowerCase())
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .category(category)
                    .uploadedBy(currentUser)
                    .relatedCourseId(relatedCourseId)
                    .relatedUserId(relatedUserId)
                    .isActive(true)
                    .build();

            FileUpload savedFile = fileUploadRepository.save(fileUpload);

            // Generate download URL
            String downloadUrl = contextPath + "/files/download/" + savedFile.getId();

            log.info("File uploaded successfully: {} by user: {}", storedFileName, currentUser.getUsername());

            return FileUploadResponse.fromEntity(savedFile, downloadUrl);

        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage());
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    public FileUploadResponse uploadProfilePicture(MultipartFile file, Long userId) {
        return uploadFile(file, FileUpload.FileCategory.PROFILE_PICTURE, null, userId);
    }

    public FileUploadResponse uploadCourseMaterial(MultipartFile file, Long courseId) {
        return uploadFile(file, FileUpload.FileCategory.COURSE_MATERIAL, courseId, null);
    }

    public FileUploadResponse uploadCourseImage(MultipartFile file, Long courseId) {
        return uploadFile(file, FileUpload.FileCategory.COURSE_IMAGE, courseId, null);
    }

    public FileUploadResponse uploadCertificate(MultipartFile file, Long userId) {
        return uploadFile(file, FileUpload.FileCategory.CERTIFICATE, null, userId);
    }

    public byte[] downloadFile(Long fileId) {
        try {
            FileUpload fileUpload = fileUploadRepository.findById(fileId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"File not found with id: " + fileId));

            Path filePath = Paths.get(fileUpload.getFilePath());
            if (!Files.exists(filePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"File not found on disk: " + fileUpload.getFilePath());
            }

            log.info("File downloaded: {} (ID: {})", fileUpload.getOriginalFileName(), fileId);

            return Files.readAllBytes(filePath);

        } catch (IOException e) {
            log.error("File download failed: {}", e.getMessage());
            throw new RuntimeException("File download failed: " + e.getMessage());
        }
    }

    public void deleteFile(Long fileId) {
        try {
            FileUpload fileUpload = fileUploadRepository.findById(fileId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"File not found with id: " + fileId));

            Path filePath = Paths.get(fileUpload.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            fileUploadRepository.delete(fileUpload);
            log.info("File deleted: {} (ID: {})", fileUpload.getOriginalFileName(), fileId);

        } catch (IOException e) {
            log.error("File deletion failed: {}", e.getMessage());
            throw new RuntimeException("File deletion failed: " + e.getMessage());
        }
    }

    public List<FileUploadResponse> getFilesByCategory(FileUpload.FileCategory category) {
        return fileUploadRepository.findByCategoryAndIsActiveTrue(category)
                .stream()
                .map(f -> FileUploadResponse.fromEntity(f, contextPath + "/files/download/" + f.getId()))
                .collect(Collectors.toList());
    }

    public List<FileUploadResponse> getFilesByCourse(Long courseId) {
        return fileUploadRepository.findByRelatedCourseId(courseId)
                .stream()
                .map(f -> FileUploadResponse.fromEntity(f, contextPath + "/files/download/" + f.getId()))
                .collect(Collectors.toList());
    }

    public List<FileUploadResponse> getFilesByUser(Long userId) {
        return fileUploadRepository.findByRelatedUserId(userId)
                .stream()
                .map(f -> FileUploadResponse.fromEntity(f, contextPath + "/files/download/" + f.getId()))
                .collect(Collectors.toList());
    }

    private void validateFile(MultipartFile file, FileUpload.FileCategory category) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Check file size
        if (file.getSize() > fileUploadProperties.getMaxSize()) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size: " + fileUploadProperties.getMaxSize());
        }

        // Check file type based on category
        String fileExtension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        List<String> allowedTypes = getAllowedTypesForCategory(category);

        if (!allowedTypes.contains(fileExtension)) {
            throw new IllegalArgumentException("File type not allowed: " + fileExtension + ". Allowed types: " + allowedTypes);
        }
    }

    private List<String> getAllowedTypesForCategory(FileUpload.FileCategory category) {
        return switch (category) {
            case PROFILE_PICTURE -> fileUploadProperties.getProfilePicturesList();
            case COURSE_MATERIAL, LESSON_ATTACHMENT -> fileUploadProperties.getCourseMaterialsList();
            case CERTIFICATE -> fileUploadProperties.getCertificatesList();
            case COURSE_IMAGE -> fileUploadProperties.getProfilePicturesList();
            default -> fileUploadProperties.getAllowedTypesList();
        };
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String timestamp = System.currentTimeMillis() + "";
        String uuid = UUID.randomUUID().toString();
        return timestamp + "_" + uuid + "." + extension;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found: " + email));
    }
}
