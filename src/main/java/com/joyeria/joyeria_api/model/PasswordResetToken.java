package com.joyeria.joyeria_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * PasswordResetToken class
 *
 * @Version: 1.0.0 - 27 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 27 feb. 2026
 */
@Entity
@Table(name = "password_reset-token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Boolean used = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // verifica si el token expiro
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    //verifica si el token es valido(si esta usado o expirado)
    public boolean isValid() {
        return !used && !isExpired();
    }
}
