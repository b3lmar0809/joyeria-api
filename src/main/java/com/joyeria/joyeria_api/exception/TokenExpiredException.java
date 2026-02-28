package com.joyeria.joyeria_api.exception;

/**
 * TokenExpiredException class
 *
 * @Version: 1.0.0 - 27 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 27 feb. 2026
 */

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException() {
        super("el toke ah expirado. por favor, solicite uno nuevo");
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
