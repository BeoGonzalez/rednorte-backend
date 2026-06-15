package com.example.ms_lista_espera.exception;

/**
 * Exception thrown when a request is syntactically valid but violates a business
 * invariant enforced by the microservice.
 * <p>
 * Examples include duplicate scheduling attempts, invalid state transitions, or
 * operations that conflict with the current lifecycle of a medical attention
 * request. The global exception handler maps this exception to an HTTP status
 * that reflects a client-correctable domain conflict.
 * </p>
 */
public class BusinessRuleException extends RuntimeException {

    /**
     * Creates a business-rule exception with a domain-specific message.
     *
     * @param message Description of the violated business rule.
     */
    public BusinessRuleException(String message) {
        super(message);
    }

    /**
     * Creates a business-rule exception with a domain-specific message and
     * original cause.
     *
     * @param message Description of the violated business rule.
     * @param cause   Original exception that caused the rule violation.
     */
    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
