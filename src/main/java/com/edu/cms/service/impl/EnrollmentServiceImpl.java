package com.edu.cms.service.impl;

import com.edu.cms.common.enums.EnrollmentStatus;
import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.common.exception.ConfilictException;
import com.edu.cms.common.exception.ForbiddenException;
import com.edu.cms.common.exception.ResourceNotFoundException;
import com.edu.cms.common.principal.CustomUserDetail;
import com.edu.cms.dto.enrollment.request.EnrollmentCreateRequest;
import com.edu.cms.dto.enrollment.response.EnrollmentDetailResponse;
import com.edu.cms.dto.enrollment.response.EnrollmentResponse;
import com.edu.cms.dto.lessonProgress.response.LessonProgressResponse;
import com.edu.cms.entity.*;
import com.edu.cms.mapper.EnrollmentMapper;
import com.edu.cms.repository.CourseRepository;
import com.edu.cms.repository.EnrollmentRepository;
import com.edu.cms.repository.LessonRepository;
import com.edu.cms.service.EnrollmentService;
import com.edu.cms.common.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final PageUtils pageUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getAllEnrollment(Integer page, Integer size, String sortBy, SortDirection sortDirection) {
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = pageUtils.createPageable(page, size, sortBy, sortDirection);
        Page<Enrollment> enrollment = enrollmentRepository.findAllByStudentId(customUserDetail.getId(), pageable);
        return enrollment.map(enrollmentMapper::toResponse);
    }

    @Override
    @Transactional
    public EnrollmentResponse createEnrollment(EnrollmentCreateRequest enrollmentCreateRequest) {
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Course course = courseRepository.findById(enrollmentCreateRequest.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));

        if (enrollmentRepository.existsByStudentIdAndCourseId(customUserDetail.getId(), enrollmentCreateRequest.getCourseId())) {
            throw new ConfilictException("Bạn đã đăng kí khóa học này");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(User.builder().id(customUserDetail.getId()).build())
                .course(course)
                .status(EnrollmentStatus.ENROLLED)
                .progressPercentage(BigDecimal.ZERO)
                .build();
        Enrollment saved = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentDetailResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin đăng kí"));
        validateStudentOwnership(enrollment);

        List<Lesson> lessons = lessonRepository.findAllByCourseIdAndIsPublishedTrue(enrollment.getCourse().getId());

        Map<Long, LessonProgress> progressMap = enrollment.getLessonProgresses().stream()
                .collect(Collectors.toMap(p -> p.getLesson().getId(), p -> p));

        List<LessonProgressResponse> lessonProgressResponse = lessons.stream().map(lesson -> {
            LessonProgress lessonProgress = progressMap.get(lesson.getId());
            return LessonProgressResponse.builder()
                    .lessonId(lesson.getId())
                    .title(lesson.getTitle())
                    .orderIndex(lesson.getOrderIndex())
                    .isCompleted(lessonProgress != null && Boolean.TRUE.equals(lessonProgress.getIsCompleted()))
                    .completedAt(lessonProgress != null ? lessonProgress.getCompletedAt() : null)
                    .build();
        }).collect(Collectors.toList());

        EnrollmentDetailResponse enrollmentDetailResponse = enrollmentMapper.toDetailResponse(enrollment);
        enrollmentDetailResponse.setLessons(lessonProgressResponse);

        return enrollmentDetailResponse;
    }

    @Override
    @Transactional
    public EnrollmentResponse completeLesson(Long enrollmentId, Long lessonId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy enrollment"));

        validateStudentOwnership(enrollment);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        // Kiểm tra lesson có thuộc course không
        if (!lesson.getCourse().getId().equals(enrollment.getCourse().getId())) {
            throw new IllegalArgumentException("Bài học không thuộc khóa học này");
        }

        // Tìm hoặc tạo LessonProgress
        LessonProgress progress = enrollment.getLessonProgresses().stream()
                .filter(p -> p.getLesson().getId().equals(lessonId))
                .findFirst()
                .orElseGet(() -> {
                    LessonProgress newProgress = LessonProgress.builder()
                            .enrollment(enrollment)
                            .lesson(lesson)
                            .isCompleted(false)
                            .build();
                    enrollment.getLessonProgresses().add(newProgress);
                    return newProgress;
                });

        if (!progress.getIsCompleted()) {
            progress.setIsCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());

            // Cập nhật progress percentage
            long totalLessons = lessonRepository.countByCourseIdAndIsPublishedTrue(enrollment.getCourse().getId());
            long completedLessons = enrollment.getLessonProgresses().stream()
                    .filter(p -> Boolean.TRUE.equals(p.getIsCompleted()))
                    .count();

            BigDecimal progressPercentage = BigDecimal.valueOf(completedLessons * 100.0 / totalLessons)
                    .setScale(2, RoundingMode.HALF_UP);
            enrollment.setProgressPercentage(progressPercentage);

            // Kiểm tra hoàn thành khóa học
            if (completedLessons == totalLessons) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                enrollment.setCompletionDate(LocalDateTime.now());
            }
        }

        Enrollment saved = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponse(saved);
    }

    private void validateStudentOwnership(Enrollment enrollment) {
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = customUserDetail.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && (enrollment.getStudent() == null || !enrollment.getStudent().getId().equals(customUserDetail.getId()))) {
            throw new ForbiddenException("Bạn không có quyền cho tác động vào enrollment này");
        }
    }
}