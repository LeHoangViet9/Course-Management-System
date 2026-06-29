package com.edu.cms.service;

import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.dto.review.request.ReviewRequest;
import com.edu.cms.dto.review.response.ReviewResponse;
import org.springframework.data.domain.Page;

public interface ReviewService {
    Page<ReviewResponse> getCourseReviews(Long courseId, Integer page, Integer size, String sortBy, SortDirection direction);
    ReviewResponse createReview(Long courseId, ReviewRequest request);
    ReviewResponse updateReview(Long id, ReviewRequest request);
    void deleteReview(Long id);
}
