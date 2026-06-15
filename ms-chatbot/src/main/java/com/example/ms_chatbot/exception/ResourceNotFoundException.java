package com.example.ms_chatbot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested chatbot-domain resource cannot be found.
 * <p>
 * The chatbot currently exposes stateless triage operations, but this exception
 * is ready for persisted resources such as conversation sessions, prompt
 * templates, triage records, or audit entries. The {@link ResponseStatus}
 * annotation documents the intended HTTP mapping while
 * {@link GlobalExceptionHandler} creates the standardized response body.
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
