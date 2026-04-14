package com.example.ms_lista_espera.dto;

import java.time.LocalDateTime;

public record SolicitudResponseDto(
        Long id,
        Long pacienteId,
        String rutPaciente,    // ¡Este dato viene del ms-pacientes!
        String nombrePaciente, // ¡Este dato viene del ms-pacientes!
        String tipoSolicitud,
        String gravedad,
        String estado,
        LocalDateTime fechaSolicitud
) {
}
