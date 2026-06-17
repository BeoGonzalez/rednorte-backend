package com.example.pacientes.dto;

import com.example.pacientes.model.EstadoPaciente;
import java.time.LocalDateTime;

public record PacienteResponseDto(
        Long id,
        Long authId,
        String rut,
        String email,
        String nombre,
        String apellido,
        EstadoPaciente estado,
        LocalDateTime fechaRegistro
) {}
