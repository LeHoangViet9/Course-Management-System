package com.edu.cms.service;

import com.edu.cms.entity.RefreshToken;
import com.edu.cms.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String email);

    RefreshToken verifyExpiration(RefreshToken token);

    void deleteByUser(String email);

    RefreshToken findByToken(String token);
}
