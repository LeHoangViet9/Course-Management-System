package com.edu.cms.controller;

import com.edu.cms.common.dto.ApiResponse;
import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.dto.notification.request.NotificationRequest;
import com.edu.cms.dto.notification.response.NotificationResponse;
import com.edu.cms.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) SortDirection direction
    ) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Hiển thị danh sách thông báo thành công",
                notificationService.getAllNotification(page, size, sortBy, direction),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> readNotification(@PathVariable Long id) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Đánh dấu thông báo đã đọc thành công",
                notificationService.isRead(id),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(@Valid @RequestBody NotificationRequest notificationRequest){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Tạo thông báo thành công",
                notificationService.createNotification(notificationRequest),
                null,
                HttpStatus.CREATED
        ),HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id){
        notificationService.deleteNotification(id);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xóa thông báo thành công",
                null,
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
}
