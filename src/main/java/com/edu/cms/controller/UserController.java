package com.edu.cms.controller;

import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.dto.user.request.UpdateUserRequest;
import com.edu.cms.dto.user.request.UserChangePasswordRequest;
import com.edu.cms.dto.user.request.UserCreateRequest;
import com.edu.cms.common.dto.ApiResponse;
import com.edu.cms.dto.user.response.UserResponse;
import com.edu.cms.common.enums.UserRole;
import com.edu.cms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) SortDirection direction
    ) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Hiện thị danh sách người dùng thành công",
                userService.getUsers(status, page, size, sortBy, direction),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Hiển thị thông tin người dùng theo id thành công",
                userService.getUserById(id),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Thêm người dùng thành công",
                userService.createUser(userCreateRequest),
                null,
                HttpStatus.CREATED
        ),HttpStatus.CREATED);
    }
    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(@PathVariable Long id,@Valid @RequestParam UserRole userRole) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhập quyền người dùng thành công",
                userService.updateUserRole(id, userRole),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(@PathVariable Long id,@Valid @RequestParam Boolean isActive) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhập trạng thái người dùng thành công",
                userService.updateUserStatus(id, isActive),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xóa người dùng thành công",
                null,
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id,@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhập người dùng thành công",
                userService.updateUser(id, updateUserRequest),
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updateUserPassword(@PathVariable Long id,@Valid @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(id, userChangePasswordRequest);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhập mật khẩu thành công",
                null,
                null,
                HttpStatus.OK
        ),HttpStatus.OK);
    }

}
