package com.example.api_gateway;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Prueba unitaria del controlador raíz del API Gateway.
 * No requiere levantar el contexto reactivo: valida directamente el contrato del endpoint.
 */
class HomeControllerTest {

    private final HomeController homeController = new HomeController();

    @Test
    void home_retornaMensajeDeBienvenidaOperativo() {
        String body = homeController.home();

        assertThat(body).contains("API Gateway de Red Norte");
        assertThat(body).contains("OPERATIVO");
        assertThat(body).contains("/api/pacientes");
    }
}
