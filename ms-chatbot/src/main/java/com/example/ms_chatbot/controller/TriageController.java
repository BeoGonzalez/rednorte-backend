package com.example.ms_chatbot.controller;

import com.example.ms_chatbot.exception.AiProviderException;
import com.example.ms_chatbot.exception.BusinessRuleException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.ai.chat.model.ChatResponse;

/**
 * Controlador REST para la gestión de triaje médico inteligente.
 * <p>
 * Este componente expone los servicios del asistente virtual de RedNorte, permitiendo
 * a los usuarios realizar consultas preliminares sobre síntomas. Utiliza Spring AI
 * para orquestar la comunicación con los modelos de lenguaje (LLM).
 * </p>
 * <p>
 * Toda interacción está protegida mediante el requerimiento de seguridad 'BearerAuth'.
 * </p>
 */
@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "Triage con Inteligencia Artificial", description = "Endpoints para consultar al asistente médico")
@SecurityRequirement(name = "BearerAuth")
public class TriageController {

    private final ChatClient chatClient;

    /**
     * Inicializa el controlador inyectando el cliente de chat mediante el Builder de Spring AI.
     *
     * @param builder Builder configurado para construir la instancia de {@link ChatClient}.
     */
    public TriageController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * Procesa los síntomas ingresados por el paciente para generar una orientación médica o pre-diagnóstico.
     * <p>
     * El proceso interno sigue estos pasos:
     * <ol>
     * <li>Establece el contexto del asistente (System Prompt) con las directrices de RedNorte.</li>
     * <li>Envía los síntomas del usuario (User Prompt) al LLM configurado.</li>
     * <li>Captura la respuesta textual y registra las métricas de consumo de tokens (Prompt y Completion).</li>
     * </ol>
     * </p>
     *
     * @param request Mapa que debe contener la clave "sintomas" con la descripción del cuadro clínico del paciente.
     * @return Un mapa consolidado con la clave "resultado" (respuesta de la IA) y "total_tokens" (uso acumulado de la sesión).
     */
    @PostMapping("/triage")
    @Operation(summary = "Procesar síntomas", description = "Envía los síntomas del paciente a la IA para recibir un pre-diagnóstico y orientación médica.")
    public Map<String, Object> procesarSintomas(@RequestBody Map<String, String> request) {
        String sintomas = request != null ? request.get("sintomas") : null;

        if (sintomas == null || sintomas.isBlank()) {
            throw new BusinessRuleException("Debe informar los síntomas del paciente para ejecutar el triaje.");
        }

        try {
            ChatResponse response = chatClient.prompt()
                    .system("Eres un asistente médico de RedNorte...")
                    .user(sintomas)
                    .call()
                    .chatResponse();

            if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
                throw new AiProviderException("No fue posible obtener una respuesta válida del asistente médico.");
            }

            String respuestaContenido = response.getResult().getOutput().getText();
            var usage = response.getMetadata().getUsage();

            System.out.println("Tokens de entrada: " + usage.getPromptTokens());
            System.out.println("Tokens de salida: " + usage.getCompletionTokens());

            return Map.of(
                    "resultado", respuestaContenido,
                    "total_tokens", usage.getTotalTokens()
            );
        } catch (AiProviderException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new AiProviderException("El asistente médico no se encuentra disponible temporalmente.", exception);
        }
    }
}
