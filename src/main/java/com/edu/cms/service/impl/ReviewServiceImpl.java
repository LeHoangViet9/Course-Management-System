package com.edu.cms.service.impl;

import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.common.exception.ForbiddenException;
import com.edu.cms.common.exception.ResourceNotFoundException;
import com.edu.cms.common.principal.CustomUserDetail;
import com.edu.cms.dto.review.request.ReviewRequest;
import com.edu.cms.dto.review.response.ReviewResponse;
import com.edu.cms.entity.Course;
import com.edu.cms.entity.Review;
import com.edu.cms.entity.User;
import com.edu.cms.mapper.ReviewMapper;
import com.edu.cms.repository.CourseRepository;
import com.edu.cms.repository.EnrollmentRepository;
import com.edu.cms.repository.ReviewRepository;
import com.edu.cms.repository.UserRepository;
import com.edu.cms.service.ReviewService;
import com.edu.cms.common.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final CourseRepository courseRepository;
    private final ReviewRepository reviewRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final PageUtils pageUtils;
    @Override
    public Page<ReviewResponse> getCourseReviews(Long courseId, Integer page, Integer size, String sortBy, SortDirection direction) {
        if(!courseRepository.existsById(courseId)){
            throw new ResourceNotFoundException("Không tìm thấy khóa học");
        }
        String actualSortBy = (sortBy != null) ? sortBy : "createdAt";
        SortDirection actualDir = (direction != null) ? direction : SortDirection.DESC;

        Pageable pageable=pageUtils.createPageable(page, size, actualSortBy, actualDir);
        Page<Review> pageReview=reviewRepository.findAllByCourseId(courseId,pageable);
        return pageReview.map(reviewMapper::toResponse);
    }

    @Override
    public ReviewResponse createReview(Long courseId, ReviewRequest request) {
        CustomUserDetail customUserDetail = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));
        boolean isEnroll=enrollmentRepository.existsByStudentIdAndCourseId(customUserDetail.getId(),courseId);
        if(!isEnroll){
            throw new ResourceNotFoundException("Bạn chưa đăng ký khóa học này");
        }
        User student=userRepository.findById(customUserDetail.getId()).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy học viên"));
        Review review = Review.builder()
                .course(course)
                .student(student)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public ReviewResponse updateReview(Long id, ReviewRequest request) {
        CustomUserDetail currentUser = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Review review=reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));
        if (!hasPermission(review, currentUser)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa đánh giá này");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toResponse(updatedReview);
    }

    @Override
    public void deleteReview(Long id) {
        CustomUserDetail currentUser = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));
        if (!hasPermission(review, currentUser)) {
            throw new ForbiddenException("Bạn không có quyền xóa đánh giá này");
        }

        reviewRepository.delete(review);
    }

    private boolean hasPermission(Review review, CustomUserDetail currentUser) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = review.getStudent().getId().equals(currentUser.getId());

        return isAdmin || isOwner;
    }
}



