package com.example.pacientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegistroPacienteDto(
        @NotBlank(message = "El RUT es totalmente oblgatorio")
        @Pattern(regexp = "^[0-9]{1,2}(\\.?[0-9]{3}){2}-[0-9kK]{1}$",
                message = "El RUT no tiene un formato válido (ej: 12.345.678-9 o 12345678-9)")
        String rut,

        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 8)
        String password,

        @NotBlank String nombre,
        @NotBlank String apellido
) {
}
