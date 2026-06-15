package com.example.ms_chatbot.exception;

/**
 * Exception thrown when a chatbot request violates a domain rule.
 * <p>
 * Examples include empty symptom descriptions, unsupported triage scenarios, or
 * inputs that cannot be processed under the safety rules defined for the
 * medical assistant.
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
