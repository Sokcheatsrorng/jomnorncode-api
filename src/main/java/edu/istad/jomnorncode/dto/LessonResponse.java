package edu.istad.jomnorncode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {

    private Long id;

    private String title;

    private String description;

    private String content;

    private String videoUrl;

    private Integer duration;

    private Integer sequenceNumber;

    private CourseResponse course;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
