package com.example.security.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centralized exception handler for the security microservice REST API.
 * <p>
 * This advice translates domain, validation, authentication, integration, and
 * unexpected failures into the common {@link ErrorResponseDto} contract. It
 * prevents controllers and security services from exposing stack traces,
 * framework-specific payloads, or sensitive authentication internals to clients.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles missing users or other security-domain resources.
     *
     * @param exception Exception raised by the application layer.
     * @param request   Current servlet request, used to extract the request path.
     * @return Standardized 404 error response.
     */
    @ExceptionHandler({ResourceNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(
            RuntimeException exception,
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
     * Handles security-domain business rule violations such as duplicate users
     * or invalid role assignments.
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
     * Handles authentication failures without revealing whether the username,
     * password, token, or authentication provider caused the rejection.
     *
     * @param exception Exception raised by Spring Security authentication flow.
     * @param request   Current servlet request, used to extract the request path.
     * @return Standardized 401 error response.
     */
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(
            AuthenticationException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status)
                .body(ErrorResponseDto.of(
                        status.value(),
                        status.getReasonPhrase(),
                        "Credenciales inválidas o autenticación no autorizada.",
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
     * Handles failed HTTP calls made with {@code RestTemplate} while preserving
     * the status code returned by the remote service.
     * <p>
     * This is the integration-error counterpart used by this microservice,
     * because it does not currently depend on OpenFeign.
     * </p>
     *
     * @param exception Exception raised by the REST client.
     * @param request   Current servlet request, used to extract the request path.
     * @return Standardized error response using the upstream HTTP status code.
     */
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorResponseDto> handleRestClientResponseException(
            RestClientResponseException exception,
            HttpServletRequest request
    ) {
        HttpStatusCode statusCode = exception.getStatusCode();
        String reason = resolveReasonPhrase(statusCode);

        return ResponseEntity.status(statusCode)
                .body(ErrorResponseDto.of(
                        statusCode.value(),
                        reason,
                        "El servicio remoto respondió con estado HTTP " + statusCode.value() + ".",
                        request.getRequestURI()
                ));
    }

    /**
     * Handles unexpected failures with a generic response.
     * <p>
     * This method is the final safety net and intentionally avoids exposing
     * stack traces, exception class names, JWT internals, SQL errors, or other
     * sensitive server details.
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
        String message = exception.getMessage() != null
                ? exception.getMessage()
                : "Ocurrió un error interno inesperado.";
        return ResponseEntity.status(status)
                .body(ErrorResponseDto.of(
                        status.value(),
                        status.getReasonPhrase(),
                        message,
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
