package com.edu.cms.dto.course.response;

import com.edu.cms.dto.lesson.response.LessonResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDetailResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer durationHours;
    private String status;

    private TeacherResponse teacher;
    private List<LessonResponse> lessons;

    private Integer totalStudents;
    private Double averageRating;

    private LocalDateTime createdAt;
}
