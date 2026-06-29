package com.edu.cms.mapper;
import com.edu.cms.dto.notification.response.NotificationResponse;
import com.edu.cms.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "userId", source = "user.id")
    NotificationResponse toResponse(Notification notification);

    List<NotificationResponse> toResponseList(List<Notification> notifications);
}
