package com.edu.cms.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopCourseReportResponse {
    private Long courseId;
    private String title;
    private String teacherName;
    private Long enrollmentCount;
    private BigDecimal price;

}
