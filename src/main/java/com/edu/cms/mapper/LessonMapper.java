package com.edu.cms.mapper;

import com.edu.cms.dto.lesson.request.LessonCreateRequest;
import com.edu.cms.dto.lesson.request.LessonUpdateRequest;
import com.edu.cms.dto.lesson.response.LessonResponse;
import com.edu.cms.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    @Mapping(source = "course.id", target = "courseId")
    LessonResponse toResponse(Lesson lesson);


    // 2. Map từ request tạo mới sang Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "isPublished", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Lesson toEntity(LessonCreateRequest request);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "isPublished", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lessonProgresses", ignore = true)
    void updateLessonFromDto(LessonUpdateRequest dto, @MappingTarget Lesson entity);

}

