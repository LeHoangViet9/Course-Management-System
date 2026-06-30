package com.edu.cms.service.impl;

import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.common.exception.ForbiddenException;
import com.edu.cms.common.exception.ResourceNotFoundException;
import com.edu.cms.common.principal.CustomUserDetail;
import com.edu.cms.dto.notification.request.NotificationRequest;
import com.edu.cms.dto.notification.response.NotificationResponse;
import com.edu.cms.entity.Notification;
import com.edu.cms.entity.User;
import com.edu.cms.mapper.NotificationMapper;
import com.edu.cms.repository.NotificationRepository;
import com.edu.cms.repository.UserRepository;
import com.edu.cms.service.NotificationService;
import com.edu.cms.common.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final PageUtils pageUtils;
    private final NotificationMapper notificationMapper;
    private final UserRepository userRepository;

    @Override
    public Page<NotificationResponse> getAllNotification(Integer page, Integer size, String sortBy, SortDirection sortDirection) {
        CustomUserDetail user = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Pageable pageable = pageUtils.createPageable(page, size, sortBy, sortDirection);
        Page<Notification> notifications = notificationRepository.findAllByUserId(user.getId(), pageable);
        return notifications.map(notificationMapper::toResponse);
    }

    @Override
    public NotificationResponse isRead(Long id) {
        CustomUserDetail user= (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Notification notification=notificationRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy thông báo"));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Bạn không có quyền đọc thông báo này");
        }

        notification.setIsRead(true);
        Notification savedNotification = notificationRepository.save(notification);
        return notificationMapper.toResponse(savedNotification);
    }

    @Override
    public NotificationResponse createNotification(NotificationRequest notificationRequest) {
        User user=userRepository.findById(notificationRequest.getUserId()).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy người dùng"));
        Notification notification=new Notification();
        notification.setUser(user);
        notification.setType(notificationRequest.getType());
        notification.setTargetUrl(notificationRequest.getTargetUrl());
        notification.setMessage(notificationRequest.getMessage());
        Notification saved=notificationRepository.save(notification);
        return notificationMapper.toResponse(saved);
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));

        CustomUserDetail currentUser = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!notification.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new ForbiddenException("Bạn không có quyền xóa thông báo này");
        }

        notificationRepository.delete(notification);
    }


}
