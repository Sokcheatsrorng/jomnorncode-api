package edu.istad.jomnorncode.controller;

import edu.istad.jomnorncode.dto.CertificateRequest;
import edu.istad.jomnorncode.dto.CertificateResponse;
import edu.istad.jomnorncode.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
@Tag(name = "Certificates", description = "Certificate Management APIs")
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Issue a certificate", description = "Only ADMIN and INSTRUCTOR can issue certificates")
    public ResponseEntity<CertificateResponse> issueCertificate(@Valid @RequestBody CertificateRequest request) {
        CertificateResponse response = certificateService.issueCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get certificate by ID")
    public ResponseEntity<CertificateResponse> getCertificateById(@PathVariable Long id) {
        CertificateResponse response = certificateService.getCertificateById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all certificates for a user with pagination")
    public ResponseEntity<Page<CertificateResponse>> getCertificatesByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<CertificateResponse> response = certificateService.getCertificatesByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get all certificates for a course with pagination")
    public ResponseEntity<Page<CertificateResponse>> getCertificatesByCourse(
            @PathVariable Long courseId,
            Pageable pageable) {
        Page<CertificateResponse> response = certificateService.getCertificatesByCourse(courseId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all certificates with pagination")
    public ResponseEntity<Page<CertificateResponse>> getAllCertificates(Pageable pageable) {
        Page<CertificateResponse> response = certificateService.getAllCertificates(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{certificateNumber}")
    @Operation(summary = "Get certificate by certificate number")
    public ResponseEntity<CertificateResponse> getCertificateByNumber(@PathVariable String certificateNumber) {
        CertificateResponse response = certificateService.getCertificateByNumber(certificateNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count/user/{userId}")
    @Operation(summary = "Get certificate count for a user")
    public ResponseEntity<Long> getCertificateCountByUser(@PathVariable Long userId) {
        long count = certificateService.getCertificateCountByUser(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/course/{courseId}")
    @Operation(summary = "Get certificate count for a course")
    public ResponseEntity<Long> getCertificateCountByCourse(@PathVariable Long courseId) {
        long count = certificateService.getCertificateCountByCourse(courseId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Update certificate")
    public ResponseEntity<CertificateResponse> updateCertificate(
            @PathVariable Long id,
            @Valid @RequestBody CertificateRequest request) {
        CertificateResponse response = certificateService.updateCertificate(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete certificate", description = "Only ADMIN can delete certificates")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Long id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/revoke")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    @Operation(summary = "Revoke certificate")
    public ResponseEntity<Void> revokeCertificate(
            @PathVariable Long id,
            @RequestParam String reason) {
        certificateService.revokeCertificate(id, reason);
        return ResponseEntity.noContent().build();
    }
}
