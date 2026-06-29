package com.edu.cms.dto.lesson.response;

import com.edu.cms.entity.Course;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonResponse {
    private Long id;

    private Long courseId;

    private String title;

    private String contentUrl;

    private String textContent;

    private Integer orderIndex;

    private Boolean isPublished;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
