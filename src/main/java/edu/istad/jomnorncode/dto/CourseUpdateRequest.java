package edu.istad.jomnorncode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateRequest {

    private String courseCode;
    private String courseTitle;
    private String description;
    private Long categoryId;
    private String thumbnailUrl;
    private Double price;
    private Integer discountPercentage;
    private Boolean isPublished;
    private Long instructorId;
}