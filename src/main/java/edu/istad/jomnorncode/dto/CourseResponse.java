package edu.istad.jomnorncode.dto;


import edu.istad.jomnorncode.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long courseId;
    private String courseCode;
    private String courseTitle;
    private String description;

    private CategoryResponse category;

    private String thumbnailUrl;
    private Double price;
    private Integer discountPercentage;
    private Boolean isPublished;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CourseResponse fromEntity(Course course) {
        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .courseCode(course.getCourseCode())
                .courseTitle(course.getCourseTitle())
                .description(course.getDescription())
                .category(CategoryResponse.fromEntity(course.getCategory()))
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .discountPercentage(course.getDiscountPercentage())
                .isPublished(course.getIsPublished())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}

