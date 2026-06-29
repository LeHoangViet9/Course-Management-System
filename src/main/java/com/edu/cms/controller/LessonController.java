package com.edu.cms.controller;

import com.edu.cms.common.dto.ApiResponse;
import com.edu.cms.dto.lesson.request.LessonUpdateRequest;
import com.edu.cms.dto.lesson.response.LessonResponse;
import com.edu.cms.dto.lessonProgress.response.LessonPreviewResponse;
import com.edu.cms.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(@PathVariable Long id){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy thông tin bài học thành công",
                lessonService.getLessonById(id),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(@PathVariable Long id, @RequestBody LessonUpdateRequest lessonUpdateRequest){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật bài học thành công",
                lessonService.updateLesson(id, lessonUpdateRequest),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> deleteLesson(@PathVariable Long id){
        lessonService.deleteLesson(id);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xóa bài học thành công",
                null,
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @PutMapping("/{id}/pulish")
    public ResponseEntity<ApiResponse<LessonResponse>> updatePulishedStatus(@PathVariable Long id, @RequestParam Boolean pulished){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật bài học thành công",
                lessonService.updatePublishedStatus(id, pulished),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @GetMapping("/{id}/preview")
    public ResponseEntity<ApiResponse<LessonPreviewResponse>> getLessonPreview(@PathVariable Long id){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xem thông tin bài học trước thành công",
                lessonService.getLessonPreview(id),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
}
