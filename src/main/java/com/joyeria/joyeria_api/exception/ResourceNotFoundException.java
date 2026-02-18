package com.joyeria.joyeria_api.exception;

//excepcion cuando no se encuentra un recurso (404)

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " no encontrado con ID: " + id);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: %s", resourceName, fieldName, fieldValue));
    }
}