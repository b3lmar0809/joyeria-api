package com.joyeria.joyeria_api.controller;

import com.joyeria.joyeria_api.dto.AuthResponse;
import com.joyeria.joyeria_api.dto.LoginRequest;
import com.joyeria.joyeria_api.dto.RegisterRequest;
import com.joyeria.joyeria_api.model.User;
import com.joyeria.joyeria_api.security.JwtUtils;
import com.joyeria.joyeria_api.service.EmailService;
import com.joyeria.joyeria_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController class
 *
 * @Version: 1.0.0 - 22 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 2026/02/22
 */

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${cors.allowed.origins}")
@RequiredArgsConstructor
@Slf4j  // ← AGREGAR ESTO
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;  // ← ASEGÚRATE DE TENER ESTO

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
}