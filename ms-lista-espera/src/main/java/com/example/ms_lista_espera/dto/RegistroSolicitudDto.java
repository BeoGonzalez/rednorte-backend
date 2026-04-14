package com.example.ms_lista_espera.dto;

public record RegistroSolicitudDto(
        Long pacienteId,
        String tipoSolicitud, // Ej: ATENCION_MEDICA, PROCEDIMIENTO, CIRUGIA
        String gravedad       // Ej: ALTA, MEDIA, BAJA
) {
}