package com.example.ms_lista_espera.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object used to expose a consistent error contract from the
 * microservice REST API.
 * <p>
 * The structure is intentionally explicit and stable so API Gateway, BFF
 * layers, frontend clients, and other microservices can consume errors without
 * depending on framework-specific exception payloads. The {@code details} map
 * is reserved for field-level validation errors or additional contextual data
 * that is safe to return to API consumers.
 * </p>
 *
 * @param timestamp Date and time when the error response was produced.
 * @param status    HTTP status code returned to the client.
 * @param error     Short HTTP or domain-oriented error description.
 * @param message   Human-readable explanation of the failure.
 * @param path      Request path where the failure occurred.
 * @param details   Optional field-level or contextual error details.
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
     * @param details Field-level or contextual information related to the error.
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
