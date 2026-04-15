package com.example.api_gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "<h1>Bienvenido a la API Gateway de Red Norte</h1>" +
                "<p>Estado del Sistema: OPERATIVO</p>" +
                "<p>Use los endpoints /api/pacientes o /api/listas-espera para interactuar.</p>";
    }
}
