package com.example.ms_chatbot.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.ai.chat.model.ChatResponse;

@RestController
@RequestMapping("/api/chatbot")
public class TriageController {

    private final ChatClient chatClient;

    // Aquí está la magia: Spring inyecta el builder que ya sabe que existen los Options
    public TriageController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping("/triage")
    public Map<String, Object> procesarSintomas(@RequestBody Map<String, String> request) {
        String sintomas = request.get("sintomas");

        // 1. Ejecución
        ChatResponse response = chatClient.prompt()
                .system("Eres un asistente médico de RedNorte...")
                .user(sintomas)
                .call()
                .chatResponse(); // Obtenemos el objeto completo

        // 2. Acceso al texto (Corregido: usamos getText() en lugar de getContent())
        String respuestaContenido = response.getResult().getOutput().getText();

        // 3. Acceso a tokens (Corregido: usamos getCompletionTokens() en lugar de getGenerationTokens())
        var usage = response.getMetadata().getUsage();

        System.out.println("Tokens de entrada: " + usage.getPromptTokens());
        System.out.println("Tokens de salida: " + usage.getCompletionTokens());

        return Map.of(
                "resultado", respuestaContenido,
                "total_tokens", usage.getTotalTokens()
        );
    }
}