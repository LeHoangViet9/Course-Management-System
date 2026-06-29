package com.edu.cms.dto.notification.request;

import com.edu.cms.common.enums.TypeNotification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequest {
    @NotNull(message = "User id không được để trống")
    private Long userId;
    @NotBlank(message = "Tin nhắn không được để trống")
    private String message;
    @NotNull(message = "Loại thông báo không được để trống")
    private TypeNotification type;
    @NotBlank(message = "Url không được để trống")
    private String targetUrl;
}
