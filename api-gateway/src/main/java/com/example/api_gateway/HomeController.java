package com.example.api_gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de entrada para la API Gateway.
 * <p>
 * Este controlador actúa como el punto de acceso raíz (root) del sistema,
 * permitiendo verificar la disponibilidad del API Gateway y proporcionando
 * información básica sobre los servicios expuestos.
 * </p>
 */
@RestController
public class HomeController {

    /**
     * Endpoint raíz que muestra un mensaje de bienvenida y el estado actual del sistema.
     * <p>
     * Este método es ideal para monitoreos simples (health checks) o para que
     * los desarrolladores verifiquen que la ruta principal está correctamente enrutada.
     * </p>
     *
     * @return Una cadena de texto en formato HTML con el estado operativo del Gateway.
     */
    @GetMapping("/")
    public String home() {
        return "<h1>Bienvenido a la API Gateway de Red Norte</h1>" +
                "<p>Estado del Sistema: OPERATIVO</p>" +
                "<p>Use los endpoints /api/pacientes o /api/listas-espera para interactuar.</p>";
    }
}