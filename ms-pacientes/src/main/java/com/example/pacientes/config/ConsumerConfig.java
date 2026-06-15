package com.example.pacientes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Configuración de los consumidores de eventos asíncronos mediante RabbitMQ.
 */
@Configuration
public class ConsumerConfig {

    /**
     * Escucha eventos de citas asignadas emitidos por ms-lista-espera.
     * El nombre del método debe coincidir con la propiedad 'spring.cloud.stream.function.definition'.
     */
    @Bean
    public Consumer<Map<String, Object>> procesarCitaAsignada() {
        return evento -> {
            System.out.println("=================================================");
            System.out.println("📥 [RabbitMQ Consumidor ms-pacientes] EVENTO RECIBIDO:");
            System.out.println("   Cita ID: " + evento.get("citaId"));
            System.out.println("   Paciente ID: " + evento.get("pacienteId"));
            System.out.println("   Doctor ID: " + evento.get("doctorId"));
            System.out.println("   Especialidad: " + evento.get("especialidad"));
            System.out.println("=================================================");

            // TODO: Aquí en el futuro puedes inyectar PacienteService o EmailService
            // para enviar un correo o notificación al paciente informándole de su nueva cita.
        };
    }
}
