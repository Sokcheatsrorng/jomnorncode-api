package edu.istad.jomnorncode.service;

import edu.istad.jomnorncode.dto.CertificateRequest;
import edu.istad.jomnorncode.dto.CertificateResponse;
import edu.istad.jomnorncode.entity.Certificate;
import edu.istad.jomnorncode.entity.Course;
import edu.istad.jomnorncode.entity.User;
import edu.istad.jomnorncode.repository.CertificateRepository;
import edu.istad.jomnorncode.repository.CourseRepository;
import edu.istad.jomnorncode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public CertificateResponse issueCertificate(CertificateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));

        // Check if certificate already issued
        if (certificateRepository.existsByUserUserIdAndCourseCourseId(request.getUserId(), request.getCourseId())) {
            throw new RuntimeException("Certificate already issued to this user for this course");
        }

        Certificate certificate = new Certificate();
        certificate.setUser(user);
        certificate.setCourse(course);
        certificate.setCertificateNumber(generateCertificateNumber());
        certificate.setIssuedDate(LocalDateTime.now());
        certificate.setCertificateUrl(request.getFileUrl());
        certificate.setCreatedAt(LocalDateTime.now());

        Certificate savedCertificate = certificateRepository.save(certificate);
        return mapToResponse(savedCertificate);
    }

    @Transactional(readOnly = true)
    public CertificateResponse getCertificateById(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + id));
        return mapToResponse(certificate);
    }

    @Transactional(readOnly = true)
    public Page<CertificateResponse> getCertificatesByUser(Long userId, Pageable pageable) {
        return certificateRepository.findByUserUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<CertificateResponse> getCertificatesByCourse(Long courseId, Pageable pageable) {
        return certificateRepository.findByCourseCourseId(courseId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<CertificateResponse> getAllCertificates(Pageable pageable) {
        return certificateRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public CertificateResponse getCertificateByNumber(String certificateNumber) {
        Certificate certificate = certificateRepository.findByCertificateNumber(certificateNumber)
                .orElseThrow(() -> new RuntimeException("Certificate not found with number: " + certificateNumber));
        return mapToResponse(certificate);
    }

    @Transactional(readOnly = true)
    public long getCertificateCountByUser(Long userId) {
        return certificateRepository.countByUserUserId(userId);
    }

    @Transactional(readOnly = true)
    public long getCertificateCountByCourse(Long courseId) {
        return certificateRepository.countByCourseCourseId(courseId);
    }

    public CertificateResponse updateCertificate(Long id, CertificateRequest request) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + id));

        if (request.getFileUrl() != null && !request.getFileUrl().isEmpty()) {
            certificate.setCertificateUrl(request.getFileUrl());
        }
        certificate.setUpdatedAt(LocalDateTime.now());

        Certificate updatedCertificate = certificateRepository.save(certificate);
        return mapToResponse(updatedCertificate);
    }

    public void deleteCertificate(Long id) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + id));
        certificateRepository.delete(certificate);
    }

    public void revokeCertificate(Long id, String reason) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found with id: " + id));
        certificate.setRevokedAt(LocalDateTime.now());
        certificate.setRevokeReason(reason);
        certificateRepository.save(certificate);
    }

    private String generateCertificateNumber() {
        return "CERT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private CertificateResponse mapToResponse(Certificate certificate) {
        return CertificateResponse.builder()
                .id(certificate.getCertificateId())
                .userId(certificate.getUser().getUserId())
                .userName(certificate.getUser().getUsername())
                .courseId(certificate.getCourse().getCourseId())
                .courseName(certificate.getCourse().getCourseTitle())
                .certificateNumber(certificate.getCertificateNumber())
                .issueDate(certificate.getIssuedDate())
                .fileUrl(certificate.getCertificateUrl())
                .revokedAt(certificate.getRevokedAt())
                .revokeReason(certificate.getRevokeReason())
                .createdAt(certificate.getCreatedAt())
                .updatedAt(certificate.getUpdatedAt())
                .build();
    }
}
