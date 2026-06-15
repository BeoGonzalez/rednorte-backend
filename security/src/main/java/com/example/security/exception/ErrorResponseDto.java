package com.example.security.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response returned by the security microservice.
 * <p>
 * This DTO defines a stable contract for API clients, the BFF, and other
 * microservices that need to consume authentication and authorization errors in
 * a predictable format. The {@code details} map is intended for field-level
 * validation failures or additional safe context that can be exposed without
 * leaking implementation details.
 * </p>
 *
 * @param timestamp Date and time when the error response was created.
 * @param status    HTTP status code returned to the client.
 * @param error     Short HTTP or domain-oriented error description.
 * @param message   Human-readable explanation of the failure.
 * @param path      Request path where the error occurred.
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
     * @param path    Request path where the error occurred.
     * @return Error response initialized with the current timestamp.
     */
    public static ErrorResponseDto of(int status, String error, String message, String path) {
        return of(status, error, message, path, Map.of());
    }

    /**
     * Creates a standard error response with additional safe details.
     *
     * @param status  HTTP status code returned to the client.
     * @param error   Short HTTP or domain-oriented error description.
     * @param message Human-readable explanation of the failure.
     * @param path    Request path where the error occurred.
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
