package com.edu.cms.service.impl;

import com.edu.cms.common.enums.SortDirection;
import com.edu.cms.common.exception.ConfilictException;
import com.edu.cms.common.exception.ForbiddenException;
import com.edu.cms.common.exception.ResourceNotFoundException;
import com.edu.cms.common.principal.CustomUserDetail;
import com.edu.cms.dto.user.request.UpdateUserRequest;
import com.edu.cms.dto.user.request.UserChangePasswordRequest;
import com.edu.cms.dto.user.request.UserCreateRequest;
import com.edu.cms.dto.user.response.UserResponse;
import com.edu.cms.entity.User;
import com.edu.cms.common.enums.UserRole;
import com.edu.cms.mapper.UserMapper;
import com.edu.cms.repository.UserRepository;
import com.edu.cms.service.UserService;
import com.edu.cms.common.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PageUtils pageUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsers(Boolean status, Integer page, Integer size, String sortBy, SortDirection direction) {
        Pageable pageable = pageUtils.createPageable(page, size, sortBy, direction);
        Page<User> users = userRepository.findAllByStatus(status, pageable);
        return users.map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        if(userRepository.existsUserByEmail(userCreateRequest.getEmail())) {
            throw new ConfilictException("Email đã tồn tại");
        }
        if(userRepository.existsUserByUsername(userCreateRequest.getUsername())) {
            throw new ConfilictException("Username đã tồn tại");
        }
        User user=new User();
        user.setEmail(userCreateRequest.getEmail());
        user.setUsername(userCreateRequest.getUsername());
        user.setFullName(userCreateRequest.getFullName());
        user.setPasswordHash(passwordEncoder.encode(userCreateRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(null);
        user.setRole(userCreateRequest.getRole()!=null ? userCreateRequest.getRole(): UserRole.STUDENT);
        user.setIsActive(true);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUserRole(Long id, UserRole userRole) {
        User user=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        CustomUserDetail currentUser=(CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getRole()==UserRole.ADMIN&&!user.getEmail().equals(currentUser.getEmail())) {
            throw new ForbiddenException("Bạn không có quyền thay đổi quyền");
        }
        user.setRole(userRole);
        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long id, Boolean isActive) {
        User user=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        CustomUserDetail currentUser=(CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(user.getRole()==UserRole.ADMIN&&!user.getEmail().equals(currentUser.getEmail())) {
            throw new ForbiddenException("Bạn không có quyền thay đổi quyền");
        }
        user.setIsActive(isActive);
        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public void changePassword(Long id, UserChangePasswordRequest userChangePasswordRequest) {
        User user=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy người dùng"));
        if(!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(),user.getPasswordHash())){
            throw new ConfilictException("Mật khẩu cũ không đúng");
        }
        if(passwordEncoder.matches(userChangePasswordRequest.getNewPassword(),user.getPasswordHash())){
            throw new ConfilictException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }
        if(!userChangePasswordRequest.getNewPassword().equals(userChangePasswordRequest.getConfirmNewPassword())){
            throw new ConfilictException("Mật khẩu xác nhận không đúng");
        }
        user.setPasswordHash(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public UserResponse updateUser(Long id,UpdateUserRequest updateUserRequest) {
        User user=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy người dùng"));
        user.setFullName(updateUserRequest.getFullName());
        user.setUpdatedAt(LocalDateTime.now());
       User saved= userRepository.save(user);
       return userMapper.toResponse(saved);
    }
}
