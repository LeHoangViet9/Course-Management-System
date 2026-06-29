package com.edu.cms.controller;

import com.edu.cms.common.dto.ApiResponse;
import com.edu.cms.dto.report.StudentProgressReportResponse;
import com.edu.cms.dto.report.TeacherReportResponse;
import com.edu.cms.dto.report.TopCourseReportResponse;
import com.edu.cms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    @GetMapping("/teacher-course-overview/{teacherId}")
    public ResponseEntity<ApiResponse<TeacherReportResponse>> getTeacherOverviewReport(@PathVariable Long teacherId) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Thống kê tổng quan về các khóa học của giáo viên",
                reportService.getTeacherOverview(teacherId),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @GetMapping("/top-course")
    public ResponseEntity<ApiResponse<List<TopCourseReportResponse>>> getTopCourseReport(
            @RequestParam Integer limit
    ) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Thống kê các khóa học được đăng ký nhiều nhất",
                reportService.getTopCourses(limit),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/student-progress/{studentId}")
    public ResponseEntity<ApiResponse<StudentProgressReportResponse>> getStudentProgressReport(@PathVariable Long studentId) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Thống kê tiến độ học của sinh viên",
                reportService.getStudentProgress(studentId),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
}
