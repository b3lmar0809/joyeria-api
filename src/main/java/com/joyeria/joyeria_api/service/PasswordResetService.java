package com.joyeria.joyeria_api.service;

import com.joyeria.joyeria_api.exception.InvalidTokenException;
import com.joyeria.joyeria_api.exception.ResourceNotFoundException;
import com.joyeria.joyeria_api.exception.TokenExpiredException;
import com.joyeria.joyeria_api.model.PasswordResetToken;
import com.joyeria.joyeria_api.model.User;
import com.joyeria.joyeria_api.repository.PasswordResetTokenRepository;
import com.joyeria.joyeria_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
/**
 * PasswordResetService class
 *
 * @Version: 1.0.0 - 27 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 27 feb. 2026
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    //token valido por 1 hora
    private static final int EXPIRATION_HOURS = 1;

    /**
     * Solicitar recuperación de contraseña
     */
    public void requestPasswordReset(String email) {
        //buscar usuario
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        //invalidar tokens anteriores del usuario
        tokenRepository.deleteByUser(user);

        //generar nuevo token
        String token = UUID.randomUUID().toString();

        //crear registro de token
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(EXPIRATION_HOURS));
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        //enviar email
        emailService.sendPasswordResetEmail(user, token);

        log.info("Token de recuperación generado para: {}", email); //eliminar
    }

    //valida el token de verificacion
    @Transactional(readOnly = true)
    public User validateResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token no encontrado"));

        if (resetToken.getUsed()) {
            throw new InvalidTokenException("Este token ya fue utilizado");
        }

        if (resetToken.isExpired()) {
            throw new TokenExpiredException();
        }

        return resetToken.getUser();
    }

    //edita contra
    public void resetPassword(String token, String newPassword) {
        //validar token
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token no encontrado"));

        if (resetToken.getUsed()) {
            throw new InvalidTokenException("Este token ya fue utilizado");
        }

        if (resetToken.isExpired()) {
            throw new TokenExpiredException();
        }

        //obtener usuario
        User user = resetToken.getUser();

        //actualizar contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        //marcar token como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("✅ Contraseña restablecida para: {}", user.getEmail());
    }

    //Llmpiar tokens expirados (se ejecuta cada hora)
    @Scheduled(cron = "0 0 * * * *")
    public void cleanExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteByExpiryDateBefore(now);
        log.info("🧹 Tokens expirados eliminados");
    }
}
