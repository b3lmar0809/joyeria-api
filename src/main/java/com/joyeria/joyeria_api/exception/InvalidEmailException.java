package com.joyeria.joyeria_api.exception;

/**
 * InvalidEmailException class
 *
 * @Version: 1.0.0 - 25 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 25 feb. 2026
 */

// cuando el email es invalido
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String email) {
        super(String.format("El email '%s' no es válido", email));
    }

    public InvalidEmailException(String message, String email) {
        super(String.format("%s: '%s'", message, email));
    }
}
