package com.edu.cms.dto.lesson.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonUpdateRequest {
    private String title;

    private String contentUrl;

    private String textContent;

    private Integer orderIndex;
}
