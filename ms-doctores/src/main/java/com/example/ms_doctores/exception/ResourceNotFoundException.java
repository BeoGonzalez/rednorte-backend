package com.example.ms_doctores.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested doctor-domain resource cannot be found in
 * the persistence layer.
 * <p>
 * This exception is intended for missing doctor profiles or future resources
 * owned by the doctors microservice. The {@link ResponseStatus} annotation
 * documents the intended HTTP mapping, while {@link GlobalExceptionHandler}
 * builds the standardized response body.
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
