package com.example.ms_chatbot.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

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

        // El chatClient usará automáticamente los options definidos en tu Bean
        String respuesta = chatClient.prompt()
                .system("Eres un asistente médico de RedNorte. Analiza los síntomas y retorna un JSON con: 'especialidad' (sugerida), 'urgencia' (Alta/Media/Baja) y un 'mensaje' breve para el paciente.")
                .user(sintomas)
                .call()
                .content();

        return Map.of("resultado", respuesta);
    }
}