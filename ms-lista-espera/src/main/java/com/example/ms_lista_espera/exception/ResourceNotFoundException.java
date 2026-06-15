package com.example.ms_lista_espera.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested domain resource cannot be found in the
 * persistence layer.
 * <p>
 * This exception is intentionally generic so it can be reused by repositories
 * and services for entities such as solicitudes, citas, pacientes referenced by
 * ID, or any future aggregate managed by the microservice. The
 * {@link ResponseStatus} annotation documents the default HTTP mapping while
 * {@link GlobalExceptionHandler} builds the standardized response body.
 * </p>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a not-found exception with a domain-specific message.
     *
     * @param message Description of the missing resource.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a not-found exception with a domain-specific message and original
     * cause.
     *
     * @param message Description of the missing resource.
     * @param cause   Original exception that caused the not-found condition.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
