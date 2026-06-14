package com.example.ms_chatbot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.ai.chat.model.ChatResponse;

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "Triage con Inteligencia Artificial", description = "Endpoints para consultar al asistente médico")
@SecurityRequirement(name = "BearerAuth") // 🟢 Obliga a pedir el JWT en Swagger
public class TriageController {

    private final ChatClient chatClient;

    // Aquí está la magia: Spring inyecta el builder que ya sabe que existen los Options
    public TriageController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping("/triage")
    @Operation(summary = "Procesar síntomas", description = "Envía los síntomas del paciente a la IA para recibir un pre-diagnóstico y orientación médica.")
    public Map<String, Object> procesarSintomas(@RequestBody Map<String, String> request) {
        String sintomas = request.get("sintomas"); // Extrae los síntomas del JSON

        // 1. Ejecución
        ChatResponse response = chatClient.prompt()
                .system("Eres un asistente médico de RedNorte...") // Contexto base para la IA
                .user(sintomas)
                .call()
                .chatResponse(); // Obtenemos el objeto completo

        // 2. Acceso al texto
        String respuestaContenido = response.getResult().getOutput().getText(); // Obtiene la respuesta de Gemini/OpenAI

        // 3. Acceso a tokens
        var usage = response.getMetadata().getUsage(); // Obtiene las métricas de consumo

        System.out.println("Tokens de entrada: " + usage.getPromptTokens()); // Log en consola
        System.out.println("Tokens de salida: " + usage.getCompletionTokens()); // Log en consola

        // Retorna el resultado junto con la métrica de consumo total
        return Map.of(
                "resultado", respuestaContenido,
                "total_tokens", usage.getTotalTokens()
        );
    }
}