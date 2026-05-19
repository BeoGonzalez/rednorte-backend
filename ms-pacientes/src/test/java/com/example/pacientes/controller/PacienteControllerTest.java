package com.example.pacientes.controller;

import com.example.pacientes.dto.PacienteRequestDto;
import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.model.EstadoPaciente;
import com.example.pacientes.service.PacienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

// 🔴 ESTE ES EL IMPORT CORRECTO EN SPRING BOOT 3.4/4.0
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PacienteController.class)
@AutoConfigureMockMvc(addFilters = false)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 🔴 REEMPLAZA @MockBean con @MockitoBean
    @MockitoBean
    private PacienteService pacienteService;

    private PacienteResponseDto responseDto;

    @BeforeEach
    void setUp() {
        responseDto = new PacienteResponseDto(
                1L, "123456789", "juan@test.com", "Juan", "Perez",
                EstadoPaciente.ACTIVO, LocalDateTime.now()
        );
    }

    @Test
    void obtenerTodos_DeberiaRetornarStatus200() throws Exception {
        Mockito.when(pacienteService.obtenerTodos()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }
}