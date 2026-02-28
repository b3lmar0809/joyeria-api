package com.joyeria.joyeria_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
/**
 * ForgotPasswordRequest class
 *
 * @Version: 1.0.0 - 27 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 27 feb. 2026
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

        @NotBlank(message = "El email es requerido")
        @Email(message = "Email inválido")
        private String email;
}
