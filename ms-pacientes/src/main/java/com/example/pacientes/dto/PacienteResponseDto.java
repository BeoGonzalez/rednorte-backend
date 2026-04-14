package com.example.pacientes.dto;

import java.time.LocalDateTime;

public record PacienteResponseDto(
        Long id,
        String rut,
        String email,
        String nombre,
        String apellido,
        LocalDateTime fechaRegistro) {

}
