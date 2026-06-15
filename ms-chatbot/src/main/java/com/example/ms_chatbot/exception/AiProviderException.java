package com.example.ms_chatbot.exception;

/**
 * Exception thrown when the chatbot cannot obtain a valid response from the AI
 * provider or Spring AI orchestration layer.
 * <p>
 * This exception separates temporary external-provider failures from local
 * application errors and prevents provider payloads, prompts, request IDs, API
 * keys, or model internals from being exposed to clients through the BFF.
 * </p>
 */
public class AiProviderException extends RuntimeException {

    /**
     * Creates an AI-provider exception with a safe client-facing message.
     *
     * @param message Description of the AI-provider failure.
     */
    public AiProviderException(String message) {
        super(message);
    }

    /**
     * Creates an AI-provider exception with a safe client-facing message and
     * original cause.
     *
     * @param message Description of the AI-provider failure.
     * @param cause   Original exception raised by the AI integration.
     */
    public AiProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
