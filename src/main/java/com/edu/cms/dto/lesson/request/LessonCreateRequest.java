package com.edu.cms.dto.lesson.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonCreateRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    @NotBlank(message = "Dường dẫn không được để trống")
    private String contentUrl;
    @NotBlank(message = "Nội dung không được để trống")
    private String textContent;
    @NotNull(message = "Thứ tự không được để trống")
    private Integer orderIndex;

}
