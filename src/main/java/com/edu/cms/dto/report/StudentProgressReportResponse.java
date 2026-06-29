package com.edu.cms.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentProgressReportResponse {
    private Long studentId;
    private String studentName;
    private Long totalEnrolledCourses;
    private Double averageProgress;
    private List<CourseProgressDetail> details;

    @Data
    @AllArgsConstructor
    public static class CourseProgressDetail {
        private Long courseId;
        private String courseTitle;
        private Double progressPercentage;
        private String status;
    }
}
