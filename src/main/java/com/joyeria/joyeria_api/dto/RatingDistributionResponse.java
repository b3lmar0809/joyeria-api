package com.joyeria.joyeria_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RatingDistributionResponse class
 *
 * @Version: 1.0.0 - 20 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 2026/02/20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingDistributionResponse {
    private Integer rating;      // 1, 2, 3, 4, o 5 estrellas
    private Long count;          // cantidad de reseñas con ese rating
}