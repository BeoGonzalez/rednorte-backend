package com.example.bff.controller;

import com.example.bff.client.ChatClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador Backend For Frontend (BFF) especializado en la gestión de interacciones con el chatbot de IA.
 * <p>
 * Este controlador centraliza y reenvía las consultas del usuario final hacia el microservicio
 * responsable de la lógica de IA (<code>ms-chatbot</code>). Actúa como una capa de abstracción
 * segura, gestionando la comunicación distribuida y el manejo de excepciones de red entre el
 * Frontend y el servicio de IA.
 * </p>
 */
@RestController
@RequestMapping("/bff/chat")
public class BffChatController {

    @Autowired
    private ChatClient chatClient;

    /**
     * Envía una consulta del usuario al microservicio de chatbot para obtener una respuesta asistida.
     * <p>
     * Este método actúa como un proxy, delegando la lógica pesada de procesamiento al microservicio
     * correspondiente. Implementa un manejo de errores robusto para capturar fallos de comunicación
     * vía {@link FeignException}.
     * </p>
     *
     * @param request Mapa con el contenido de la consulta o contexto necesario para el chatbot.
     * @return ResponseEntity con la respuesta proveniente del servicio de chat,
     * o el mensaje de error propagado si la comunicación falla.
     */
    @PostMapping("/preguntar")
    public ResponseEntity<?> preguntar(@RequestBody Map<String, Object> request) {
        try {
            return chatClient.preguntar(request);
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(e.contentUTF8());
        }
    }
}