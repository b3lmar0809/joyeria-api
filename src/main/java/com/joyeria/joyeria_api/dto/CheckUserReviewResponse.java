package com.joyeria.joyeria_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CheckUserReviewResponse class
 *
 * @Version: 1.0.0 - 20 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 2026/02/20
 */



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckUserReviewResponse {
    private Boolean hasReviewed;
}