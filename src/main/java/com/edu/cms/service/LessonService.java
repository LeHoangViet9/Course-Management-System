package com.edu.cms.service;

import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.dto.lesson.request.LessonCreateRequest;
import com.edu.cms.dto.lesson.request.LessonUpdateRequest;
import com.edu.cms.dto.lesson.response.LessonResponse;
import com.edu.cms.dto.lessonProgress.response.LessonPreviewResponse;
import org.springframework.data.domain.Page;

public interface LessonService {

    LessonResponse getLessonById(Long lessonId);
    LessonResponse createLesson(Long courseId, LessonCreateRequest request);
    LessonResponse updateLesson(Long lessonId, LessonUpdateRequest request);
    LessonResponse updatePublishedStatus(Long lessonId, Boolean publishedStatus);
    void  deleteLesson(Long lessonId);

    LessonPreviewResponse getLessonPreview(Long lessonId);
}
