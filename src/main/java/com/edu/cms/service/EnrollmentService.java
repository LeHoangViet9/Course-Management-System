package com.edu.cms.service;

import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.dto.enrollment.request.EnrollmentCreateRequest;
import com.edu.cms.dto.enrollment.response.EnrollmentDetailResponse;
import com.edu.cms.dto.enrollment.response.EnrollmentResponse;
import org.springframework.data.domain.Page;

public interface EnrollmentService {
    Page<EnrollmentResponse> getAllEnrollment(Integer page, Integer size, String sortBy, SortDirection sortDirection);
    EnrollmentResponse createEnrollment(EnrollmentCreateRequest enrollmentCreateRequest);
    EnrollmentDetailResponse getEnrollmentById(Long id);
    EnrollmentResponse completeLesson(Long enrollmentId, Long lessonId);
}
