package com.edu.cms.mapper;

import com.edu.cms.dto.user.response.UserResponse;
import com.edu.cms.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}