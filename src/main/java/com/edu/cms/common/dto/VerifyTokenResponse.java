package com.edu.cms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyTokenResponse {
    private boolean isValid;
}
