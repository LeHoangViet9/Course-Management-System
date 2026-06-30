package com.edu.cms.service.impl;

import com.edu.cms.common.enums.UserRole;
import com.edu.cms.common.exception.ConfilictException;
import com.edu.cms.common.exception.ForbiddenException;
import com.edu.cms.common.exception.ResourceNotFoundException;
import com.edu.cms.common.principal.CustomUserDetail;
import com.edu.cms.dto.lesson.request.LessonCreateRequest;
import com.edu.cms.dto.lesson.request.LessonUpdateRequest;
import com.edu.cms.dto.lesson.response.LessonResponse;
import com.edu.cms.dto.lessonProgress.response.LessonPreviewResponse;
import com.edu.cms.entity.Course;
import com.edu.cms.entity.Lesson;
import com.edu.cms.mapper.LessonMapper;
import com.edu.cms.repository.CourseRepository;
import com.edu.cms.repository.EnrollmentRepository;
import com.edu.cms.repository.LessonRepository;
import com.edu.cms.service.LessonService;
import com.edu.cms.common.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PageUtils pageUtils;
    private final LessonMapper lessonMapper;

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("Bạn chưa đăng nhập hoặc phiên đăng nhập đã hết hạn");
        }

        CustomUserDetail currentUser = (CustomUserDetail) authentication.getPrincipal();
        if (currentUser == null) {
            throw new ForbiddenException("Thông tin người dùng không hợp lệ");
        }

        boolean isAdmin = currentUser.getAuthorities() != null && currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        boolean isTeacherOfCourse = false;
        if (lesson.getCourse() != null && lesson.getCourse().getTeacher() != null && currentUser.getId() != null) {
            isTeacherOfCourse = lesson.getCourse().getTeacher().getId().equals(currentUser.getId());
        }

        boolean isAdminOrTeacher = isAdmin || isTeacherOfCourse;

        if (Boolean.FALSE.equals(lesson.getIsPublished())) {
            if (!isAdminOrTeacher) {
                throw new ForbiddenException("Bài học này chưa được kích hoạt hiển thị");
            }
        }

        if (!isAdminOrTeacher) {
            if (lesson.getCourse() == null) {
                throw new ResourceNotFoundException("Bài học này không thuộc về khóa học nào");
            }
            boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(currentUser.getId(), lesson.getCourse().getId());
            if (!isEnrolled) {
                throw new ForbiddenException("Bạn cần đăng ký khóa học này để xem nội dung bài học");
            }
        }

        return lessonMapper.toResponse(lesson);
    }

    @Override
    @Transactional
    public LessonResponse createLesson(Long courseId, LessonCreateRequest request) {
        Course course=courseRepository.findById(courseId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy khóa học"));
        if(lessonRepository.existsLessonByTitle(request.getTitle())){
            throw new ConfilictException("Tên bài học đã tồn tại");
        }
        if(lessonRepository.existsLessonByContentUrl(request.getContentUrl())){
            throw new ConfilictException("Đường dẫn bài học đã tồn tại");
        }
        validateTeacherOrAdminPermission(course);
        Lesson lesson=lessonMapper.toEntity(request);
        lesson.setCourse(course);
        lesson.setIsPublished(Boolean.FALSE);
        lesson.setCreatedAt(LocalDateTime.now());
        Lesson saved=lessonRepository.save(lesson);
        return lessonMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(Long lessonId, LessonUpdateRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));
        if (lessonRepository.existsByTitleAndIdNot(request.getTitle(), lessonId)) {
            throw new ConfilictException("Tên bài học đã tồn tại ở một bài học khác");
        }
        if (lessonRepository.existsByContentUrlAndIdNot(request.getContentUrl(), lessonId)) {
            throw new ConfilictException("Đường dẫn bài học đã tồn tại ở một bài học khác");
        }
        validateTeacherOrAdminPermission(lesson.getCourse());

        lessonMapper.updateLessonFromDto(request, lesson);

        lesson.setUpdatedAt(LocalDateTime.now());
        Lesson saved = lessonRepository.save(lesson);
        return lessonMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public LessonResponse updatePublishedStatus(Long lessonId, Boolean publishedStatus) {
        Lesson lesson=lessonRepository.findById(lessonId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy bài học"));
        validateTeacherOrAdminPermission(lesson.getCourse());
        lesson.setIsPublished(publishedStatus);
        Lesson saved=lessonRepository.save(lesson);
        return lessonMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId) {
        Lesson lesson=lessonRepository.findById(lessonId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy bài học"));
        validateTeacherOrAdminPermission(lesson.getCourse());
        lessonRepository.delete(lesson);
    }

    @Override
    public LessonPreviewResponse getLessonPreview(Long lessonId) {
        Lesson lesson=lessonRepository.findById(lessonId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy bài học"));
        String rawContent = lesson.getTextContent() != null ? lesson.getTextContent() : "";
        int maxLength = Math.min(rawContent.length(), 200);
        String previewText = rawContent.substring(0, maxLength);

        if (rawContent.length() > 200) {
            previewText += "... (Đăng ký khóa học để xem toàn bộ nội dung)";
        }

        return LessonPreviewResponse.builder()
                .lessonId(lesson.getId())
                .title(lesson.getTitle())
                .previewContent(previewText)
                .isFullContent(false)
                .build();

    }

    private void validateTeacherOrAdminPermission(Course course){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null||!authentication.isAuthenticated()){
            throw new ForbiddenException("Bạn chưa đăng nhập hoặc phiên đăng nhập đã hết hạn");
        }
        CustomUserDetail  customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        boolean isAdmin=customUserDetail.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN")||a.getAuthority().equals("ADMIN"));
        if(isAdmin){
            return;
        }
        if(course.getTeacher()==null||!course.getTeacher().getId().equals(customUserDetail.getId())){
            throw new ForbiddenException("Bạn không có quyền cho khóa học này");
        }
    }
}
