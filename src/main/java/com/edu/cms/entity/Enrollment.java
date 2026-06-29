package com.edu.cms.entity;

import com.edu.cms.common.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_course",
                        columnNames = {"student_id", "course_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enrollment_date", nullable = false, updatable = false)
    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(
            name = "progress_percentage",
            nullable = false,
            precision = 5,
            scale = 2
    )
    private BigDecimal progressPercentage = BigDecimal.ZERO;

    @OneToMany(
            mappedBy = "enrollment",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LessonProgress> lessonProgresses = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        enrollmentDate = LocalDateTime.now();
    }
}
