package com.edu.cms.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherReportResponse {
    private Long teacherId;
    private String teacherName;
    private Long totalCourses;
    private Long totalStudents;
    private Double averageRating;
    private Double totalRevenue;
}
