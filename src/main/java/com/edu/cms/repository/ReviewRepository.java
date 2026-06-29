package com.edu.cms.repository;

import com.edu.cms.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
    Page<Review> findAllByCourseId(Long courseId, Pageable pageable);

    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);
}
