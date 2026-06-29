package com.edu.cms.dto.lessonProgress.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonPreviewResponse {
    private Long lessonId;
    private String title;
    private String previewContent;
    private Boolean isFullContent;
}
