package com.edu.cms.dto.notification.response;

import com.edu.cms.common.enums.TypeNotification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long notificationId;
    private Long userId;
    private String message;
    private TypeNotification type;
    private String targetUrl;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
