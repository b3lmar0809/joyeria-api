package com.joyeria.joyeria_api.exception;

/**
 * EmailSendException class
 *
 * @Version: 1.0.0 - 25 feb. 2026
 * @Author: Matias Belmar - mati.belmar0625@gmail.com
 * @Since: 1.0.0 25 feb. 2026
 */

//exception cuando falla el envio de un email
public class EmailSendException extends RuntimeException {

    public EmailSendException(String recipient, String emailType) {
        super(String.format("Error al enviar email de tipo '%s' a '%s'", emailType, recipient));
    }

    public EmailSendException(String recipient, String emailType, Throwable cause) {
        super(String.format("Error al enviar email de tipo '%s' a '%s': %s",
                emailType, recipient, cause.getMessage()), cause);
    }
}
