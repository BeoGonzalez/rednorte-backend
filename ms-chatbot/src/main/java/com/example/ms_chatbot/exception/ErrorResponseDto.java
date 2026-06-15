package com.example.ms_chatbot.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO that standardizes error responses returned by the chatbot microservice.
 * <p>
 * This contract is intentionally stable so the BFF can forward or normalize
 * chatbot failures without depending on Spring MVC, Spring AI, or provider
 * specific exception payloads. The {@code details} map is reserved for
 * validation errors or safe contextual data.
 * </p>
 *
 * @param timestamp Date and time when the error response was created.
 * @param status    HTTP status code returned to the client.
 * @param error     Short HTTP or domain-oriented error description.
 * @param message   Human-readable explanation of the failure.
 * @param path      Request path where the failure occurred.
 * @param details   Optional validation or contextual details.
 */
public record ErrorResponseDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> details
) {

    /**
     * Creates a standard error response without additional details.
     *
     * @param status  HTTP status code returned to the client.
     * @param error   Short HTTP or domain-oriented error description.
     * @param message Human-readable explanation of the failure.
     * @param path    Request path where the failure occurred.
     * @return Error response initialized with the current timestamp.
     */
    public static ErrorResponseDto of(int status, String error, String message, String path) {
        return of(status, error, message, path, Map.of());
    }

    /**
     * Creates a standard error response with safe contextual details.
     *
     * @param status  HTTP status code returned to the client.
     * @param error   Short HTTP or domain-oriented error description.
     * @param message Human-readable explanation of the failure.
     * @param path    Request path where the failure occurred.
     * @param details Validation or contextual information safe for API clients.
     * @return Error response initialized with the current timestamp.
     */
    public static ErrorResponseDto of(
            int status,
            String error,
            String message,
            String path,
            Map<String, String> details
    ) {
        return new ErrorResponseDto(LocalDateTime.now(), status, error, message, path, details);
    }
}
