package com.edu.cms.controller;

import com.edu.cms.dto.user.request.UserLoginRequest;
import com.edu.cms.dto.user.request.UserRegisterRequest;
import com.edu.cms.common.dto.ApiResponse;
import com.edu.cms.dto.user.response.LoginResponse;
import com.edu.cms.dto.user.response.UserResponse;
import com.edu.cms.common.dto.VerifyTokenResponse;
import com.edu.cms.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegisterRequest user){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Đăng kí thành công",
                authService.register(user),
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody UserLoginRequest user){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Đăng nhập thành công",
                authService.login(user),
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerifyTokenResponse>> verify(@Valid @RequestParam String token){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xác thực thành công",
                authService.verifyToken(token),
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }
    @GetMapping("me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentMyInformation(){
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Hiển thị thông tin cá nhân thành công",
                authService.getMyInformation(),
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestParam("refreshToken") String requestToken) {

        LoginResponse loginResponse = authService.refreshToken(requestToken);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Gia hạn mã truy cập thành công",
                loginResponse,
                null,
                HttpStatus.OK
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam("refreshToken") String refreshToken){
        authService.logout(refreshToken);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Đăng xuất thành công",
                null,
                null,
                HttpStatus.OK
        ));
    }

}
