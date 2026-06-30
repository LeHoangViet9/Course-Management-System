package com.edu.cms.service.impl;

import com.edu.cms.common.enums.CourseStatus;
import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.common.enums.UserRole;
import com.edu.cms.common.exception.ConfilictException;
import com.edu.cms.common.exception.ResourceNotFoundException;
import com.edu.cms.dto.course.request.CourseCreateRequest;
import com.edu.cms.dto.course.request.CourseUpdateRequest;
import com.edu.cms.dto.course.response.CourseDetailResponse;
import com.edu.cms.dto.course.response.CourseResponse;
import com.edu.cms.dto.course.response.CourseSumaryResponse;
import com.edu.cms.dto.lesson.response.LessonResponse;
import com.edu.cms.entity.Course;
import com.edu.cms.entity.Lesson;
import com.edu.cms.entity.User;
import com.edu.cms.mapper.CourseMapper;
import com.edu.cms.mapper.LessonMapper;
import com.edu.cms.mapper.ReviewMapper;
import com.edu.cms.repository.CourseRepository;
import com.edu.cms.repository.LessonRepository;
import com.edu.cms.repository.ReviewRepository;
import com.edu.cms.repository.UserRepository;
import com.edu.cms.service.CourseService;
import com.edu.cms.common.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final PageUtils pageUtils;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CourseSumaryResponse> getCourseSumary(String keyword, Long teacherId, CourseStatus status, Integer page, Integer size, String sortBy, SortDirection direction) {
        Pageable pageable = pageUtils.createPageable(page, size, sortBy, direction);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            status = CourseStatus.PUBLISHED;
        }

        String cleanWord = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        Page<Course> course = courseRepository.searchAndFilterCourses(cleanWord, teacherId, status, pageable);
        return course.map(courseMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy khóa học"));
        return courseMapper.toDetailResponse(course);
    }

    @Override
    @Transactional
    public CourseResponse createCourse(CourseCreateRequest courseCreateRequest) {
        User teacher = userRepository.findById(courseCreateRequest.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên"));
        if (teacher.getRole() != UserRole.TEACHER) {
            throw new IllegalArgumentException("Tài khoản được gán phải là giảng viên");
        }
        if (courseRepository.existsByTitle(courseCreateRequest.getTitle())) {
            throw new ConfilictException("Tên khóa học đã tồn tại!");
        }
        Course course = new Course();
        course.setTeacher(teacher);

        LocalDateTime now = LocalDateTime.now();
        course.setCreatedAt(now);
        course.setUpdatedAt(now);

        course.setDescription(courseCreateRequest.getDescription());
        course.setTitle(courseCreateRequest.getTitle());
        course.setDurationHours(courseCreateRequest.getDurationHours());
        course.setPrice(courseCreateRequest.getPrice());
        course.setStatus(CourseStatus.DRAFT);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest courseUpdateRequest) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã khóa học"));
        if (courseUpdateRequest.getTeacherId() != null) {
            User teacher = userRepository.findById(courseUpdateRequest.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên"));
            course.setTeacher(teacher);
        }
        courseMapper.updateCourseFromDto(courseUpdateRequest, course);
        course.setUpdatedAt(LocalDateTime.now());

        Course savedCourse = courseRepository.save(course);
        return courseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional
    public CourseResponse updateStatusCourse(Long courseId, CourseStatus courseStatus) {
        Course course=courseRepository.findById(courseId).orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy mã khóa học"));
        course.setStatus(courseStatus);
        course.setUpdatedAt(LocalDateTime.now());
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toResponse(savedCourse);
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        Course course=courseRepository.findById(courseId).orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy mã khóa học"));
        courseRepository.delete(course);
    }
    @Override
    public Page<LessonResponse> getAllLessonByCourseId(Long courseId, Integer page, Integer size, String sortBy, SortDirection sortDirection) {
        if(!courseRepository.existsById(courseId)){
            throw new ResourceNotFoundException("Không tìm thấy khóa học");
        }
        Pageable pageable=pageUtils.createPageable(page,size,sortBy,sortDirection);
        Page<Lesson> lessons=lessonRepository.findAllByCourseIdAndIsPublishedTrue(courseId,pageable);
        return lessons.map(lessonMapper::toResponse);
    }

}
