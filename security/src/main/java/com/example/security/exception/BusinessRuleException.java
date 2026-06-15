package com.example.security.exception;

/**
 * Exception thrown when a request violates a business invariant in the security
 * domain.
 * <p>
 * Typical examples include attempting to register a username that already
 * exists, assigning an unsupported role, or executing an operation that is not
 * valid for the current state of an identity record.
 * </p>
 */
public class BusinessRuleException extends RuntimeException {

    /**
     * Creates a business-rule exception with a domain-specific message.
     *
     * @param message Description of the violated rule.
     */
    public BusinessRuleException(String message) {
        super(message);
    }

    /**
     * Creates a business-rule exception with a domain-specific message and
     * original cause.
     *
     * @param message Description of the violated rule.
     * @param cause   Original exception that caused the rule violation.
     */
    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
