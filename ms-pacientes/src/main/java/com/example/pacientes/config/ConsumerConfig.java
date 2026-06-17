package com.example.pacientes.config;

import com.example.pacientes.model.Notificacion;
import com.example.pacientes.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Consumer;

@Configuration
public class ConsumerConfig {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Bean
    public Consumer<Map<String, Object>> procesarCitaAsignada() {
        return evento -> {
            System.out.println("[ms-pacientes] Cita asignada automáticamente recibida");
            System.out.println("  citaId     : " + evento.get("citaId"));
            System.out.println("  pacienteId : " + evento.get("pacienteId"));
            System.out.println("  doctorId   : " + evento.get("doctorId"));
            System.out.println("  especialidad: " + evento.get("especialidad"));
        };
    }

    @Bean
    public Consumer<Map<String, Object>> procesarCitaActualizada() {
        return evento -> {
            String estado = String.valueOf(evento.get("nuevoEstado"));
            Long pacienteId = Long.parseLong(String.valueOf(evento.get("pacienteId")));

            System.out.println("[ms-pacientes] Decisión del doctor recibida — paciente: " + pacienteId + " estado: " + estado);

            String mensaje = "ACEPTADA".equals(estado)
                    ? "Su agenda ha sido aceptada"
                    : "Su agenda ha sido rechazada";

            notificacionRepository.save(new Notificacion(pacienteId, mensaje));
        };
    }
}
