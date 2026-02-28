package com.joyeria.joyeria_api.controller;

import com.joyeria.joyeria_api.dto.*;
import com.joyeria.joyeria_api.model.User;
import com.joyeria.joyeria_api.security.JwtUtils;
import com.joyeria.joyeria_api.service.EmailService;
import com.joyeria.joyeria_api.service.PasswordResetService;
import com.joyeria.joyeria_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController class
 *
 * @Version: 1.0.2 - 27 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 2026/02/22
 */

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${cors.allowed.origins}")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Crear usuario
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        // Registrar
        User registered = userService.registerUser(user);
        log.info("Usuario registrado: {}", registered.getEmail());//eliminar

        // ENVIAR EMAIL
        try {
            log.info("Intentando enviar email de bienvenida a: {}", registered.getEmail());
            emailService.sendWelcomeEmail(registered);
            log.info("Email de bienvenida enviado correctamente");
        } catch (Exception e) {
            log.error("Error enviando email de bienvenida: {}", e.getMessage(), e);
            // No interrumpir el registro aunque falle el email
        }

        // Generar token
        String token = jwtUtils.generateToken(registered);

        // Crear respuesta
        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                registered.getId(),
                registered.getEmail(),
                registered.getFirstName(),
                registered.getLastName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Autenticar
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Obtener usuario autenticado
        User user = (User) authentication.getPrincipal();

        // Generar token
        String token = jwtUtils.generateToken(user);

        // Crear respuesta
        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("🔵 Solicitud de recuperación para: {}", request.getEmail());

        passwordResetService.requestPasswordReset(request.getEmail());

        return ResponseEntity.ok(new MessageResponse(
                "Si el email existe, recibirás instrucciones para restablecer tu contraseña"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("🔵 Restableciendo contraseña con token");

        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(new MessageResponse(
                "Contraseña restablecida exitosamente. Ya puedes iniciar sesión con tu nueva contraseña."
        ));
    }


    // GET /api/auth/validate-reset-token/{token}
    //validar si un token de reset es válido

    @GetMapping("/validate-reset-token/{token}")
    public ResponseEntity<MessageResponse> validateResetToken(@PathVariable String token) {
        log.info("🔵 Validando token de reset");

        passwordResetService.validateResetToken(token);

        return ResponseEntity.ok(new MessageResponse("Token válido"));
    }
}