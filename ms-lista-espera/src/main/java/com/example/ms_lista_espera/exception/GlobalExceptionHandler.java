package com.example.ms_lista_espera.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centralized REST exception handler for the waiting-list microservice.
 * <p>
 * This advice converts domain, validation, integration, and unexpected
 * exceptions into the common {@link ErrorResponseDto} contract. Keeping this
 * mapping in one place prevents controllers from leaking implementation details
 * and ensures API clients receive predictable HTTP status codes and response
 * bodies across all endpoints.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles requests for resources that do not exist in the local data store.
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
     * Handles domain-rule violations that the client can correct by changing the
     * request or retrying after the conflicting state changes.
     *
     * @param exception Exception raised by the business layer.
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
     * Field errors are returned in insertion order using the DTO field name as
     * the key and the validation annotation message as the value.
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
     * Handles failed OpenFeign calls to other microservices while preserving the
     * HTTP status code returned by the upstream service whenever it is valid.
     * <p>
     * The response intentionally avoids exposing stack traces or low-level
     * transport details to API clients.
     * </p>
     *
     * @param exception Exception raised by an OpenFeign client.
     * @param request   Current servlet request, used to extract the request path.
     * @return Standardized error response using the upstream HTTP status code.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponseDto> handleFeignException(
            FeignException exception,
            HttpServletRequest request
    ) {
        int upstreamStatusCode = exception.status();
        HttpStatus status = resolveFeignStatus(upstreamStatusCode);

        return ResponseEntity.status(status)
                .body(ErrorResponseDto.of(
                        status.value(),
                        status.getReasonPhrase(),
                        "El microservicio remoto respondió con estado HTTP " + status.value() + ".",
                        request.getRequestURI()
                ));
    }

    /**
     * Handles any uncaught exception using a generic server error response.
     * <p>
     * This method is the final safety net for unexpected failures and never
     * exposes stack traces, exception class names, SQL errors, or internal
     * implementation details to the client.
     * </p>
     *
     * @param exception Exception not handled by a more specific handler.
     * @param request   Current servlet request, used to extract the request path.
     * @return Standardized 500 error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(ErrorResponseDto.of(
                        status.value(),
                        status.getReasonPhrase(),
                        "Ocurrió un error interno inesperado.",
                        request.getRequestURI()
                ));
    }

    /**
     * Resolves the status received from Feign into a valid HTTP status.
     * <p>
     * Feign returns {@code -1} when no response was received from the remote
     * service, so that case is mapped to {@code 503 Service Unavailable}.
     * </p>
     *
     * @param statusCode Raw status code reported by Feign.
     * @return Matching Spring HTTP status or a safe integration-failure fallback.
     */
    private HttpStatus resolveFeignStatus(int statusCode) {
        HttpStatus resolvedStatus = HttpStatus.resolve(statusCode);
        return resolvedStatus != null ? resolvedStatus : HttpStatus.SERVICE_UNAVAILABLE;
    }
}
