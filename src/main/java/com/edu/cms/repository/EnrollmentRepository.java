package com.edu.cms.repository;

import com.edu.cms.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Page<Enrollment> findAllByStudentId(Long studentId, Pageable pageable);

    List<Enrollment> findAllByStudentId(Long studentId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);


}
