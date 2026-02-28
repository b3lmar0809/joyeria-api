package com.joyeria.joyeria_api.exception;

/**
 * InvalidTokenException class
 *
 * @Version: 1.0.0 - 27 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 27 feb. 2026
 */

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(){
        super(">el token de recuperacion es invalida o ha expirado ");
    }
}
