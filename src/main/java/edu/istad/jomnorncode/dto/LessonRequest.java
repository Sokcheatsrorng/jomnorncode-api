package edu.istad.jomnorncode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequest {

    @NotBlank(message = "Lesson title is required")
    private String title;

    private String description;

    private String content;

    private String videoUrl;

    private Integer duration;

    private Integer sequenceNumber;

    @NotNull(message = "Course ID is required")
    private Long courseId;
}
