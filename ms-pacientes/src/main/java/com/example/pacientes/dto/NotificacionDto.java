package com.example.pacientes.dto;

import java.time.LocalDateTime;

public record NotificacionDto(
        Long id,
        Long pacienteId,
        String mensaje,
        boolean leida,
        LocalDateTime fechaCreacion
) {}
