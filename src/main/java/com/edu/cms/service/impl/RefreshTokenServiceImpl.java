package com.edu.cms.service.impl;

import com.edu.cms.common.exception.UnauthorizedException;
import com.edu.cms.entity.RefreshToken;
import com.edu.cms.repository.RefreshTokenRepository;
import com.edu.cms.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken createRefreshToken(String email) {

        refreshTokenRepository.deleteByEmail(email);

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .email(email)
                        .refreshToken(UUID.randomUUID().toString())
                        .expired(
                                Instant.now()
                                        .plus(7, ChronoUnit.DAYS)
                        )
                        .revoked(false)
                        .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(
            RefreshToken token
    ) {

        if (Boolean.TRUE.equals(token.getRevoked())) {
            throw new UnauthorizedException(
                    "Refresh token đã bị thu hồi"
            );
        }

        if (token.getExpired().isBefore(Instant.now())) {

            token.setRevoked(true);
            refreshTokenRepository.save(token);

            throw new UnauthorizedException(
                    "Refresh token đã hết hạn"
            );
        }

        return token;
    }

    @Override
    public void deleteByUser(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() ->
                        new UnauthorizedException("Refresh token không tồn tại"));
    }
}
