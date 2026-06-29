package com.edu.cms.repository;

import com.edu.cms.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Page<Lesson> findAllByCourseIdAndIsPublishedTrue(Long courseId, Pageable pageable);

    boolean existsLessonByTitle(String title);

    boolean existsLessonByContentUrl(String contentUrl);

    boolean existsByTitleAndIdNot(String title, Long lessonId);

    boolean existsByContentUrlAndIdNot(String contentUrl, Long lessonId);

    List<Lesson> findAllByCourseIdAndIsPublishedTrue(Long courseId);

    long countByCourseIdAndIsPublishedTrue(Long id);
}
