package com.edu.cms.service;

import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.dto.notification.request.NotificationRequest;
import com.edu.cms.dto.notification.response.NotificationResponse;
import org.springframework.data.domain.Page;

public interface NotificationService {
    Page<NotificationResponse> getAllNotification(Integer page, Integer size, String sortBy, SortDirection sortDirection);
    NotificationResponse isRead(Long id);
    NotificationResponse createNotification(NotificationRequest notificationRequest);
    void deleteNotification(Long id);
}
