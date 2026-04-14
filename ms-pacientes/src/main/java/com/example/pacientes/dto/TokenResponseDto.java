package com.example.pacientes.dto;

public record TokenResponseDto(
        String token,
        String tipo // Siempre será "Bearer"
) {}
