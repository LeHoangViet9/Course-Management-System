package com.edu.cms.service.impl;

import com.edu.cms.common.enums.UserRole;
import com.edu.cms.common.exception.BadRequestException;
import com.edu.cms.common.exception.ResourceNotFoundException;
import com.edu.cms.dto.report.StudentProgressReportResponse;
import com.edu.cms.dto.report.TeacherReportResponse;
import com.edu.cms.dto.report.TopCourseReportResponse;
import com.edu.cms.entity.Enrollment;
import com.edu.cms.entity.User;
import com.edu.cms.repository.CourseRepository;
import com.edu.cms.repository.EnrollmentRepository;
import com.edu.cms.repository.UserRepository;
import com.edu.cms.service.ReportService;
import com.edu.cms.common.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final CourseRepository courseRepository;
    private final PageUtils pageUtil;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public List<TopCourseReportResponse> getTopCourses(Integer limit) {
        int actualLimit=(limit!=null&&limit>0)?limit:10;
        Pageable pageable=pageUtil.createPageable(0,actualLimit,null,null);
        return courseRepository.findTopCoursesByEnrollments(pageable);
    }

    @Override
    public StudentProgressReportResponse getStudentProgress(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản học viên với ID: " + studentId));

        if (user.getRole() != UserRole.STUDENT) {
            throw new BadRequestException("Tài khoản mang ID " + studentId + " không phải là tài khoản học viên (STUDENT)");
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(studentId);
        List<StudentProgressReportResponse.CourseProgressDetail> details = enrollments.stream()
                .map(e -> new StudentProgressReportResponse.CourseProgressDetail(
                        e.getCourse().getId(),
                        e.getCourse().getTitle(),
                        e.getProgressPercentage().doubleValue(),
                        e.getStatus().name()
                ))
                .collect(Collectors.toList());

        long totalPage = details.size();
        double avgProgress = details.stream()
                .mapToDouble(StudentProgressReportResponse.CourseProgressDetail::getProgressPercentage)
                .average()
                .orElse(0.0);

        return StudentProgressReportResponse.builder()
                .studentId(user.getId())
                .studentName(user.getFullName())
                .totalEnrolledCourses(totalPage)
                .averageProgress(avgProgress)
                .details(details)
                .build();
    }

    @Override
    public TeacherReportResponse getTeacherOverview(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản giảng viên với ID: " + teacherId));

        if (teacher.getRole() != UserRole.TEACHER) {
            throw new BadRequestException("Tài khoản mang ID " + teacherId + " không phải là tài khoản giảng viên (TEACHER)");
        }

        long totalCourse = courseRepository.countByTeacherId(teacherId);
        long totalUniqueStudents = courseRepository.countUniqueStudentsByTeacherId(teacherId);
        double sumRevenue = courseRepository.sumRevenueByTeacherId(teacherId);
        double averageRating = courseRepository.averageRatingByTeacherId(teacherId);

        return TeacherReportResponse.builder()
                .teacherId(teacher.getId())
                .teacherName(teacher.getFullName())
                .totalCourses(totalCourse)
                .totalStudents(totalUniqueStudents)
                .averageRating(averageRating)
                .totalRevenue(sumRevenue)
                .build();
    }
}
