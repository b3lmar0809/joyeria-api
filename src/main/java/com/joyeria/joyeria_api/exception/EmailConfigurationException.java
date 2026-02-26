package com.joyeria.joyeria_api.exception;

/**
 * EmailConfigurationException class
 *
 * @Version: 1.0.0 - 25 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 25 feb. 2026
 */
//problema con la configuracion de email
public class EmailConfigurationException extends RuntimeException {

    public EmailConfigurationException(String message) {
        super("Error de configuración de email: " + message);
    }

    public EmailConfigurationException(String message, Throwable cause) {
        super("Error de configuración de email: " + message, cause);
    }
}