package com.edu.cms.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenRequest {
    @NotBlank(message = "Không được để trống")
    private String refreshToken;
}
