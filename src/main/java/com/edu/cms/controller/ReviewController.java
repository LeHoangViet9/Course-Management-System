package com.edu.cms.controller;

import com.edu.cms.common.dto.ApiResponse;
import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.dto.review.request.ReviewRequest;
import com.edu.cms.dto.review.response.ReviewResponse;
import com.edu.cms.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    @GetMapping("/{courseId}/course")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getAllRevivewByCourse(
            @PathVariable Long courseId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) SortDirection direction
    ){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách đánh giá của khóa học",
                reviewService.getCourseReviews(courseId,page, size, sortBy, direction),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @PostMapping("/{courseId}/course")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReviewByCourse(@PathVariable Long courseId, @Valid @RequestBody ReviewRequest reviewRequest){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Tạo đánh giá cho khóa học",
                reviewService.createReview(courseId,reviewRequest),
                null,
                HttpStatus.CREATED
        ),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(@PathVariable Long id,@Valid @RequestBody ReviewRequest reviewRequest){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật đánh giá",
                reviewService.updateReview(id,reviewRequest),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id){
        reviewService.deleteReview(id);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xóa đánh giá",
                null,
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
}
