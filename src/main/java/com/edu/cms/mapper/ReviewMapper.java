package com.edu.cms.mapper;

import com.edu.cms.dto.review.response.ReviewResponse;
import com.edu.cms.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    ReviewResponse toResponse(Review review);
}
