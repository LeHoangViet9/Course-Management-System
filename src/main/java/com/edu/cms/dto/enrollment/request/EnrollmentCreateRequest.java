package com.edu.cms.dto.enrollment.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentCreateRequest {
    @NotNull(message = "Mã khóa học không được trống")
    private Long courseId;




}
