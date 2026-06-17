package com.example.pacientes.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centralized exception handler for the patients microservice REST API.
 * <p>
 * This advice translates domain, validation, persistence-conflict, downstream
 * HTTP, and unexpected failures into the shared {@link ErrorResponseDto}
 * contract. It prevents controllers from exposing framework-specific payloads,
 * SQL errors, stack traces, or internal implementation details to API clients.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles requests for patient-domain resources that do not exist.
     *
     * @param exception Exception raised by the service or repository layer.
     * @param request   Current servlet request, used to extract the request path.
     * @return Standardized 404 error response.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status)
                .body(ErrorResponseDto.of(
                        status.value(),
                        status.getReasonPhrase(),
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }

    /**
     * Handles patient-domain business rule violations.
     *
     * @param exception Exception raised by business logic.
     * @param request   Current servlet request, used to extract the request path.
     * @return Standardized 409 error response.
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessRule(
            BusinessRuleException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status)
                .body(ErrorResponseDto.of(
                        status.value(),
                        status.getReasonPhrase(),
                        exception.getMessage(),
                        request.getRequestURI()
                ));
    }

    /**
     * Handles validation failures produced by {@code @Valid} request DTOs.
     * <p>
     * Field errors are returned using the DTO field name as the key and the
     * validation annotation message as the value.
     * </p>
     *
     * @param exception Exception produced by Spring MVC validation.
     * @param request   Current servlet request, used to extract the request path.
     * @return Standardized 400 error response with field-level details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationErrors(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> details = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                details.put(error.getField(), error.getDefaultMessage())
        );

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ErrorResponseDto.of(
                        status.value(),
                        status.getReasonPhrase(),
                        "La solicitud contiene campos inválidos.",
                        request.getRequestURI(),
                        details
                ));
    }

    /**
     * Handles database integrity conflicts such as duplicate unique patient
     * fields.
     * <p>
     * The response is intentionally generic to avoid leaking database constraint
     * names or SQL implementation details.
     * </p>
     *
     * @param exception Exception raised by the persistence layer.
     * @param request   Current servlet request, used to extract the request path.
     * @return Standardized 409 error response.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status)
                .body(ErrorResponseDto.of(
                        status.value(),
                        status.getReasonPhrase(),
                        "Ya existe un paciente con los datos únicos informados.",
                        request.getRequestURI()
                ));
    }

    /**
     * Handles HTTP failures returned by external services, preserving the
     * original status code when available.
     *
     * @param exception Exception raised by a REST client.
     * @param request   Current servlet request, used to extract the request path.
     * @return Standardized error response using the upstream HTTP status code.
     */
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorResponseDto> handleRestClientResponseException(
            RestClientResponseException exception,
            HttpServletRequest request
    ) {
        HttpStatusCode statusCode = exception.getStatusCode();

        return ResponseEntity.status(statusCode)
                .body(ErrorResponseDto.of(
                        statusCode.value(),
                        resolveReasonPhrase(statusCode),
                        "El servicio remoto respondió con estado HTTP " + statusCode.value() + ".",
                        request.getRequestURI()
                ));
    }

    /**
     * Handles any uncaught exception with a generic server error response.
     * <p>
     * This final safety net intentionally avoids exposing stack traces, SQL
     * errors, exception class names, or internal implementation details.
     * </p>
     *
     * @param exception Exception not handled by a more specific method.
     * @param request   Current servlet request, used to extract the request path.
     * @return Standardized 500 error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {
        exception.printStackTrace();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(ErrorResponseDto.of(
                        status.value(),
                        status.getReasonPhrase(),
                        exception.getMessage() != null ? exception.getMessage() : "Ocurrió un error interno inesperado.",
                        request.getRequestURI()
                ));
    }

    /**
     * Resolves a readable reason phrase for a status code returned by a remote
     * HTTP service.
     *
     * @param statusCode HTTP status code reported by the REST client.
     * @return Standard HTTP reason phrase when available, otherwise a generic label.
     */
    private String resolveReasonPhrase(HttpStatusCode statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        return status != null ? status.getReasonPhrase() : "HTTP " + statusCode.value();
    }
}
