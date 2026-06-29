package com.edu.cms.service;

import com.edu.cms.dto.report.StudentProgressReportResponse;
import com.edu.cms.dto.report.TeacherReportResponse;
import com.edu.cms.dto.report.TopCourseReportResponse;

import java.util.List;

public interface ReportService {
    List<TopCourseReportResponse> getTopCourses(Integer limit);
    StudentProgressReportResponse getStudentProgress(Long studentId);
    TeacherReportResponse getTeacherOverview(Long teacherId);
}
