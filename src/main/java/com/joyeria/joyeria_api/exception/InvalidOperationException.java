package com.joyeria.joyeria_api.exception;

//excepcion para operaciones invalidas (400)

public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}