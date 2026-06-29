package com.edu.cms.service.impl;

import com.edu.cms.common.exception.ConfilictException;
import com.edu.cms.common.exception.ForbiddenException;
import com.edu.cms.common.exception.ResourceNotFoundException;
import com.edu.cms.common.exception.UnauthorizedException;
import com.edu.cms.common.jwt.JwtTokenProvider;
import com.edu.cms.common.principal.CustomUserDetail;
import com.edu.cms.dto.user.request.UserLoginRequest;
import com.edu.cms.dto.user.request.UserRegisterRequest;
import com.edu.cms.dto.user.response.LoginResponse;
import com.edu.cms.dto.user.response.UserResponse;
import com.edu.cms.common.dto.VerifyTokenResponse;
import com.edu.cms.entity.RefreshToken;
import com.edu.cms.entity.User;
import com.edu.cms.common.enums.UserRole;
import com.edu.cms.mapper.UserMapper;
import com.edu.cms.repository.RefreshTokenRepository;
import com.edu.cms.repository.UserRepository;
import com.edu.cms.service.AuthService;
import com.edu.cms.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    @Override
    public LoginResponse login(UserLoginRequest request) {

        User user = userRepository.findByEmail(
                request.getEmail()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Email không tồn tại"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new UnauthorizedException(
                    "Email hoặc mật khẩu sai");
        }

        if (!user.getIsActive()) {

            throw new ForbiddenException(
                    "Vui lòng xác thực email trước khi đăng nhập");
        }

        String accessToken =
                jwtTokenProvider.generateToken(
                        user.getEmail());

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    public UserResponse register(UserRegisterRequest request) {

        if(userRepository.existsUserByEmail(request.getEmail())){
            throw new ConfilictException("Email đã tồn tại");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(UserRole.STUDENT);

        user.setIsActive(false);

        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        String verifyToken =
                jwtTokenProvider.generateVerifyToken(
                        savedUser.getEmail()
                );

        log.info("Verify Token for {} : {}", savedUser.getEmail(), verifyToken);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public VerifyTokenResponse verifyToken(String token) {

        if(!jwtTokenProvider.validateToken(token) || !"verify".equals(jwtTokenProvider.getTokenType(token))){
            throw new UnauthorizedException(
                    "Token không hợp lệ hoặc hết hạn");
        }

        String email =
                jwtTokenProvider.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email không tồn tại"));

        if(Boolean.TRUE.equals(user.getIsActive())){
            throw new ConfilictException(
                    "Tài khoản đã được xác thực");
        }

        user.setIsActive(true);

        userRepository.save(user);

        return new VerifyTokenResponse(true);
    }

    @Override
    public UserResponse getMyInformation() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetail) {
            CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();

            String email = userDetails.getEmail();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại"));

            return userMapper.toResponse(user);
        }

        throw new UnauthorizedException("Người dùng chưa đăng nhập hoặc token không hợp lệ");
    }

    public LoginResponse refreshToken(String requestToken) {
        RefreshToken token = refreshTokenService.findByToken(requestToken);

        RefreshToken verifiedToken = refreshTokenService.verifyExpiration(token);

        String newAccessToken = jwtTokenProvider.generateToken(verifiedToken.getEmail());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(verifiedToken.getRefreshToken())
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
        SecurityContextHolder.clearContext();
    }

}
