package com.edu.cms.controller;

import com.edu.cms.common.dto.ApiResponse;
import com.edu.cms.common.enums.CourseStatus;
import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.dto.course.request.CourseCreateRequest;
import com.edu.cms.dto.course.request.CourseUpdateRequest;
import com.edu.cms.dto.course.response.CourseDetailResponse;
import com.edu.cms.dto.course.response.CourseResponse;
import com.edu.cms.dto.course.response.CourseSumaryResponse;
import com.edu.cms.dto.lesson.request.LessonCreateRequest;
import com.edu.cms.dto.lesson.response.LessonResponse;
import com.edu.cms.service.CourseService;
import com.edu.cms.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/courses")
public class CourseController {
    private final CourseService courseService;
    private final LessonService lessonService;
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseSumaryResponse>>> getAllCourse(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) CourseStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false)SortDirection direction
            ){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Hiển thị danh sách các khóa học thành công",
                courseService.getCourseSumary(keyword,teacherId,status,page,size,sortBy,direction),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> getCourseById(@PathVariable Long id){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Hiển thị khóa học theo id thành công",
                courseService.getCourseById(id),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseCreateRequest courseCreateRequest){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Thêm mới khóa học thành công",
                courseService.createCourse(courseCreateRequest),
                null,
                HttpStatus.CREATED
        ),HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(@PathVariable Long id,@Valid @RequestBody CourseUpdateRequest courseUpdateRequest){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhập khóa học thành công",
                courseService.updateCourse(id, courseUpdateRequest),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CourseResponse>> updateStatus(@PathVariable Long id, @RequestParam CourseStatus courseStatus){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhập trạng thái khóa học thành công",
                courseService.updateStatusCourse(id, courseStatus),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id){
        courseService.deleteCourse(id);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xóa khóa học thành công",
                null,
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<ApiResponse<Page<LessonResponse>>> getAllLessonByCourseId(
            @PathVariable Long courseId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) SortDirection sortDirection) {

        Page<LessonResponse> lessons = courseService.getAllLessonByCourseId(courseId, page, size, sortBy, sortDirection);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Lấy danh sách bài học của khóa học thành công",
                lessons,
                null,
                HttpStatus.OK
        ));
    }

    @PostMapping("/{courseId}/lessons")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(@PathVariable Long courseId, @RequestBody LessonCreateRequest lessonRequest) {
       return new ResponseEntity<>(new ApiResponse<>(
               true,
               "Tạo bài học thành công",
               lessonService.createLesson(courseId, lessonRequest),
               null,
               HttpStatus.CREATED
       ),HttpStatus.CREATED);
    }



}
