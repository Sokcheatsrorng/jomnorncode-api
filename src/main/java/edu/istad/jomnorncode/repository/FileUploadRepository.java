package edu.istad.jomnorncode.repository;

import edu.istad.jomnorncode.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    Optional<FileUpload> findByStoredFileName(String storedFileName);

    List<FileUpload> findByCategory(FileUpload.FileCategory category);

    List<FileUpload> findByRelatedCourseId(Long courseId);     // correct (scalar Long field)

    List<FileUpload> findByRelatedUserId(Long userId);         // correct (scalar Long field)

    List<FileUpload> findByUploadedByUserId(Long userId);      // correct (association)

    List<FileUpload> findByCategoryAndIsActiveTrue(FileUpload.FileCategory category);
}