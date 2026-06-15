package com.example.pacientes.exception;

/**
 * Exception thrown when a request violates a patient-domain business rule.
 * <p>
 * Typical examples include duplicate RUT or email values, invalid patient state
 * transitions, or operations that conflict with the lifecycle rules of a
 * patient profile.
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
