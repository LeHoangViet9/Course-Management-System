package com.edu.cms.service;

import com.edu.cms.dto.user.request.UserLoginRequest;
import com.edu.cms.dto.user.request.UserRegisterRequest;
import com.edu.cms.dto.user.response.LoginResponse;
import com.edu.cms.dto.user.response.UserResponse;
import com.edu.cms.common.dto.VerifyTokenResponse;

public interface AuthService {
    LoginResponse login(UserLoginRequest userLoginRequest);
    UserResponse register(UserRegisterRequest userRegisterRequest);
    VerifyTokenResponse verifyToken(String token);
    UserResponse getMyInformation();
    LoginResponse refreshToken(String requestToken);
    void logout(String refreshToken);
}
