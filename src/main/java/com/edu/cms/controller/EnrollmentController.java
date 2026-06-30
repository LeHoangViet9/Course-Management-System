package com.edu.cms.controller;

import com.edu.cms.common.dto.ApiResponse;
import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.dto.enrollment.request.EnrollmentCreateRequest;
import com.edu.cms.dto.enrollment.response.EnrollmentDetailResponse;
import com.edu.cms.dto.enrollment.response.EnrollmentResponse;
import com.edu.cms.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EnrollmentResponse>>> getAllEnrollment(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) SortDirection direction
    ){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách khóa học sinh viên đẫ đăng kí thành công",
                enrollmentService.getAllEnrollment(page,size,sortBy,direction),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);


    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> enrollCourse(@Valid @RequestBody EnrollmentCreateRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Đăng kí khóa học thành công",
                enrollmentService.createEnrollment(request),
                null,
                HttpStatus.CREATED
        ),HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EnrollmentDetailResponse>> getEnrollmentById(@PathVariable Long id){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy thông tin khóa học sinh viên đẫ đăng kí thành công",
                enrollmentService.getEnrollmentById(id),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @PutMapping("/{enrollmentId}/complete_lesson/{lessonId}")
    public ResponseEntity<ApiResponse<Object>> completeLesson(@PathVariable Long enrollmentId,@PathVariable Long lessonId){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Đánh dấu hoàn thành bài học thành công",
                enrollmentService.completeLesson(enrollmentId,lessonId),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
}
