package com.edu.cms.mapper;

import com.edu.cms.common.enums.LessonStatus;
import com.edu.cms.dto.course.request.CourseUpdateRequest;
import com.edu.cms.dto.course.response.CourseDetailResponse;
import com.edu.cms.dto.course.response.CourseResponse;
import com.edu.cms.dto.course.response.CourseSumaryResponse;
import com.edu.cms.dto.course.response.TeacherResponse;
import com.edu.cms.dto.lesson.response.LessonResponse;
import com.edu.cms.entity.Course;
import com.edu.cms.entity.Lesson;
import com.edu.cms.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseResponse toResponse(Course course);

    @Mapping(target = "lessons", source = "lessons", qualifiedByName = "filterPublishedLessons")
    CourseDetailResponse toDetailResponse(Course course);

    void updateCourseFromDto(CourseUpdateRequest dto, @MappingTarget Course entity);

    @Mapping(target = "teacherName", source = "teacher.fullName")
    @Mapping(target = "totalLessons", source = "lessons", qualifiedByName = "calculateTotalLessons")
    CourseSumaryResponse toSummaryResponse(Course course);

    List<CourseSumaryResponse> toSummaryResponseList(List<Course> courses);

    TeacherResponse toTeacherResponse(User user);

    @Mapping(target = "courseId", source = "course.id")
    LessonResponse toLessonResponse(Lesson lesson);


    @Named("calculateTotalLessons")
    default Integer calculateTotalLessons(List<Lesson> lessons) {
        return lessons != null ? lessons.size() : 0;
    }

    @Named("filterPublishedLessons")
    default List<LessonResponse> filterPublishedLessons(List<Lesson> lessons) {
        if (lessons == null) return null;
        return lessons.stream()
                .filter(lesson -> Boolean.TRUE.equals(lesson.getIsPublished()))
                .map(this::toLessonResponse)
                .collect(Collectors.toList());
    }
}
