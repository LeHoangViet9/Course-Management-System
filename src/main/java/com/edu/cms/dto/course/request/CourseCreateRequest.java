package com.edu.cms.dto.course.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseCreateRequest {
    @NotBlank(message = "Tên khóa học không được để trống")
    private String title;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", message = "Giá khóa học không được nhỏ hơn 0")
    private BigDecimal price;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng khóa học phải từ 1 giờ trở lên")
    private Integer durationHours;

    @NotNull(message = "Mã giáo viên không được để trống")
    private Long teacherId;
}
