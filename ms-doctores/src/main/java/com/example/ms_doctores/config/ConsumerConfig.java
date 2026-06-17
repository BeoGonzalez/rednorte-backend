package com.example.ms_doctores.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Configuration
public class ConsumerConfig {

    /**
     * Receives manual doctor-decision events emitted by ListaEsperaService.actualizarParcial.
     * Routing key: cita.actualizada → exchange: citas-events-exchange
     * Payload: { solicitudId, pacienteId, doctorId, nuevoEstado, tipoSolicitud }
     */
    @Bean
    public Consumer<Map<String, Object>> procesarCitaActualizada() {
        return evento -> {
            String estado = String.valueOf(evento.get("nuevoEstado"));
            Long doctorId = Long.parseLong(String.valueOf(evento.get("doctorId")));

            System.out.println("[ms-doctores] Decisión registrada en el panel del doctor");
            System.out.println("  solicitudId : " + evento.get("solicitudId"));
            System.out.println("  pacienteId  : " + evento.get("pacienteId"));
            System.out.println("  doctorId    : " + doctorId);
            System.out.println("  nuevoEstado : " + estado);
            System.out.println("  especialidad: " + evento.get("tipoSolicitud"));

            if ("ACEPTADA".equals(estado)) {
                System.out.println("[ms-doctores] Doctor " + doctorId + ": cita confirmada en agenda.");
                // TODO: inyectar DoctorService para actualizar disponibilidad o historial del doctor
            } else if ("RECHAZADA".equals(estado)) {
                System.out.println("[ms-doctores] Doctor " + doctorId + ": solicitud rechazada registrada.");
            }
        };
    }
}
