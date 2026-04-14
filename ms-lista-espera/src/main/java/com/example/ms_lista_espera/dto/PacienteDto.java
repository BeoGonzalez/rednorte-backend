package com.example.ms_lista_espera.dto;

public record PacienteDto(
        Long id,
        String rut,
        String nombre,
        String apellido
) {
}
