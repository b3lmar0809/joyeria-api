package com.joyeria.joyeria_api.dto;
/**
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Version: 1.0.0 2026/02/18
 * @Since: 1.0.02026/02/18
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotNull(message = "El ID del producto es requerido")
    private Long productId;

    @NotNull(message = "El ID del usuario es requerido")
    private Long userId;

    @NotNull(message = "La calificación es requerida")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer rating;

    @NotBlank(message = "El comentario es requerido")
    private String comment;
}