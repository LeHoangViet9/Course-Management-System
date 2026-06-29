package com.edu.cms.mapper;

import com.edu.cms.dto.enrollment.response.EnrollmentDetailResponse;
import com.edu.cms.dto.enrollment.response.EnrollmentResponse;
import com.edu.cms.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.title", target = "courseTitle")
    @Mapping(source = "progressPercentage", target = "progress") // Map từ progressPercentage sang progress
    @Mapping(source = "enrollmentDate", target = "enrolledAt")
    EnrollmentResponse toResponse(Enrollment enrollment);

    List<EnrollmentResponse> toResponseList(List<Enrollment> enrollments);

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.title", target = "courseTitle")
    @Mapping(source = "progressPercentage", target = "progressPercentage")
    @Mapping(source = "enrollmentDate", target = "enrollmentDate")
    // Tạm thời bỏ qua (ignore) trường lessons để chúng ta tự gộp thủ công logic kết hợp trạng thái bài học ở tầng Service
    @Mapping(target = "lessons", ignore = true)
    EnrollmentDetailResponse toDetailResponse(Enrollment enrollment);}
