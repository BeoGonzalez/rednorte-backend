package com.example.pacientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginPacienteDto(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato inválido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}
