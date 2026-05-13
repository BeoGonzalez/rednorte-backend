package com.example.pacientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PacienteRequestDto(
        @NotBlank(message = "El RUT es obligatorio") String rut,
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El apellido es obligatorio") String apellido,
        @Email(message = "Debe ser un email válido") @NotBlank String email
) {}
