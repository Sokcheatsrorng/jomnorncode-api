package edu.istad.jomnorncode.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "file.upload")
@Getter
@Setter
public class FileUploadProperties {

    private String dir = "uploads";
    private Long maxSize = 52428800L; // 50MB default
    private String allowedTypes = "jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,ppt,pptx,zip,rar,mp4,mov,avi,flv,wmv,webm";
    private String profilePictures = "jpg,jpeg,png";
    private String courseMaterials = "pdf,doc,docx,ppt,pptx,xls,xlsx,zip,rar,mp4,mov,avi,flv,wmv,webm";
    private String certificates = "pdf,jpg,jpeg,png";

    public List<String> getAllowedTypesList() {
        return Arrays.asList(allowedTypes.split(","));
    }

    public List<String> getProfilePicturesList() {
        return Arrays.asList(profilePictures.split(","));
    }

    public List<String> getCourseMaterialsList() {
        return Arrays.asList(courseMaterials.split(","));
    }

    public List<String> getCertificatesList() {
        return Arrays.asList(certificates.split(","));
    }
}
