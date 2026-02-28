package com.joyeria.joyeria_api.repository;

import com.joyeria.joyeria_api.model.PasswordResetToken;
import com.joyeria.joyeria_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * PasswordResetTokenRepository class
 *
 * @Version: 1.0.0 - 27 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 2026/02/27
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository <PasswordResetToken, Long>{

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserAndUsedFalse(User user);

    void deleteByExpiryDateBefore(LocalDateTime date);

    void deleteByUser(User user);
}
