package com.edu.cms.dto.enrollment.response;

import com.edu.cms.common.enums.EnrollmentStatus;
import com.edu.cms.dto.lessonProgress.response.LessonProgressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class EnrollmentDetailResponse {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private EnrollmentStatus status;
    private BigDecimal progressPercentage;
    private LocalDateTime enrollmentDate;
    private LocalDateTime completionDate;
    private List<LessonProgressResponse> lessons;
}
