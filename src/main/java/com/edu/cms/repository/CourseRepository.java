package com.edu.cms.repository;

import com.edu.cms.common.enums.CourseStatus;
import com.edu.cms.dto.report.TopCourseReportResponse;
import com.edu.cms.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTitle(String title);


    @Query("SELECT c FROM Course c WHERE " +
            "(:search IS NULL OR " +
            " LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR " +
            " LOWER(c.description) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) ) " +
            "AND (:teacherId IS NULL OR c.teacher.id = :teacherId) " +
            "AND (:status IS NULL OR c.status = :status)")
    Page<Course> searchAndFilterCourses(
            @Param("search") String search,
            @Param("teacherId") Long teacherId,
            @Param("status") CourseStatus status,
            Pageable pageable
    );

    @Query("SELECT new com.edu.cms.dto.report.TopCourseReportResponse(" +
            "c.id, c.title, c.teacher.fullName, COUNT(e.id), c.price) " +
            "FROM Course c LEFT JOIN Enrollment e ON c.id = e.course.id " +
            "GROUP BY c.id, c.title, c.teacher.fullName, c.price " +
            "ORDER BY COUNT(e.id) DESC")
    List<TopCourseReportResponse> findTopCoursesByEnrollments(Pageable pageable);
    // 1. Đếm số lượng khóa học của giáo viên
    long countByTeacherId(Long teacherId);

    // 2. Đếm tổng số học viên (DUY NHẤT) đã đăng ký các khóa học của giảng viên này
    @Query("SELECT COUNT(DISTINCT e.student.id) FROM Enrollment e WHERE e.course.teacher.id = :teacherId")
    long countUniqueStudentsByTeacherId(@Param("teacherId") Long teacherId);

    // 3. Tính tổng doanh thu từ các khóa học của giảng viên này
    @Query("SELECT COALESCE(SUM(c.price), 0.0) FROM Enrollment e JOIN e.course c WHERE c.teacher.id = :teacherId")
    double sumRevenueByTeacherId(@Param("teacherId") Long teacherId);

    // 4. Tính điểm đánh giá trung bình (rating) dựa trên tất cả khóa học của giảng viên này
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.course.teacher.id = :teacherId")
    double averageRatingByTeacherId(@Param("teacherId") Long teacherId);
}
