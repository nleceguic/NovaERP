package com.nleceguic.novaerp.repository;

import com.nleceguic.novaerp.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenId(String tokenId);
    void deleteAllByUserId(Long userId);
}
