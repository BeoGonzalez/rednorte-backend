package com.example.ms_doctores.exception;

/**
 * Exception thrown when a request violates a doctor-domain business rule.
 * <p>
 * Typical examples include attempting to register a doctor profile for an
 * already-linked authentication account, reusing a medical license number, or
 * submitting an operation that conflicts with the doctor's lifecycle state.
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
