package com.joyeria.joyeria_api.exception;


//excepcion cuando se intenta crear un recurso duplicado (409)

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format(
                "%s con %s '%s' ya existe",
                resourceName, fieldName, fieldValue
        ));
    }
}