package com.example.ms_lista_espera.dto;

import java.time.LocalDateTime;

public record SolicitudResponseDto(
        Long id,
        Long pacienteId,
        String rutPaciente,
        String nombrePaciente,
        String tipoSolicitud,
        String gravedad,
        String estado,
        LocalDateTime fechaSolicitud,
        Long doctorId           // populated when a doctor acts; used as FASE 4 event payload
) {}
