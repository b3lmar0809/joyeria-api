package com.joyeria.joyeria_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * ProductReviewStatsResponse class
 *
 * @Version: 1.0.0 - 20 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 2026/02/20
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewStatsResponse {
    private Double average;                                    // promedio de calificación (ej: 4.5)
    private Long count;                                        // total de reseñas
    private List<RatingDistributionResponse> distribution;     // distribución por estrellas
}