package com.edu.cms.service;

import com.edu.cms.common.enums.CourseStatus;
import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.dto.course.request.CourseCreateRequest;
import com.edu.cms.dto.course.request.CourseUpdateRequest;
import com.edu.cms.dto.course.response.CourseDetailResponse;
import com.edu.cms.dto.course.response.CourseResponse;
import com.edu.cms.dto.course.response.CourseSumaryResponse;
import com.edu.cms.dto.lesson.response.LessonResponse;
import com.edu.cms.dto.review.request.ReviewRequest;
import com.edu.cms.dto.review.response.ReviewResponse;
import org.springframework.data.domain.Page;

public interface CourseService {
    Page<CourseSumaryResponse> getCourseSumary(String keyword,Long teacherId,CourseStatus status,Integer page, Integer size, String orderBy, SortDirection direction);
    CourseDetailResponse getCourseById(Long courseId);
    CourseResponse createCourse(CourseCreateRequest courseCreateRequest);
    CourseResponse updateCourse(Long courseId, CourseUpdateRequest courseUpdateRequest);
    CourseResponse updateStatusCourse(Long courseId, CourseStatus courseStatus);
    void deleteCourse(Long courseId);
    Page<LessonResponse> getAllLessonByCourseId(Long courseId, Integer page, Integer size, String sortBy, SortDirection sortDirection);



}
