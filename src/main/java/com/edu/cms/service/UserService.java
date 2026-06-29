package com.edu.cms.service;

import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.dto.user.request.UpdateUserRequest;
import com.edu.cms.dto.user.request.UserChangePasswordRequest;
import com.edu.cms.dto.user.request.UserCreateRequest;
import com.edu.cms.dto.user.response.UserResponse;
import com.edu.cms.common.enums.UserRole;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    Page<UserResponse> getUsers(Boolean status, Integer page, Integer size, String sortBy, SortDirection direction);
    UserResponse getUserById(Long id);
    UserResponse createUser(UserCreateRequest userCreateRequest);
    UserResponse updateUserRole(Long id, UserRole userRole);
    UserResponse updateUserStatus(Long id, Boolean isActive);
    void deleteUser(Long id);

    void changePassword(Long id, UserChangePasswordRequest userChangePasswordRequest);
    UserResponse updateUser(Long id,UpdateUserRequest updateUserRequest);

}
