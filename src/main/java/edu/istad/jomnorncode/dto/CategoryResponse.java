package edu.istad.jomnorncode.dto;

import edu.istad.jomnorncode.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;

    private String name;

    private String description;

    private String iconUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public static CategoryResponse fromEntity(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getCategoryId())
                .name(category.getCategoryName())
                .description(category.getDescription())
                .iconUrl(category.getIconUrl())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
