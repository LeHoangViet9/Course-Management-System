package com.edu.cms.dto.course.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseUpdateRequest {
    private String title;

    private String description;

    private BigDecimal price;

    private Integer durationHours;

    private Long teacherId;
}
