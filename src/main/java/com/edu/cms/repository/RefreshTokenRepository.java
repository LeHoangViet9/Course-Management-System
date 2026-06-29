package com.edu.cms.repository;

import com.edu.cms.entity.RefreshToken;
import com.edu.cms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByRefreshToken(String token);

    Optional<RefreshToken> findByEmail(String email);

    @Transactional
    @Modifying
    void deleteByEmail(String email);
}
