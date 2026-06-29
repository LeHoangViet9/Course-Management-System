package com.edu.cms.dto.course.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseSumaryResponse {
    private Long id;
    private String title;
    private BigDecimal price;
    private String status;
    private String teacherName;
    private Integer totalLessons;
}
