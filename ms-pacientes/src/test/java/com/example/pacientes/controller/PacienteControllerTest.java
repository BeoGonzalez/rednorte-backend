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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// 🔴 NUEVOS IMPORTS PARA ENGAÑAR A LA CAPA DE SEGURIDAD
import com.example.pacientes.security.JwtFilter;
import com.example.pacientes.service.JwtService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PacienteController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva los filtros JWT durante las pruebas unitarias
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PacienteService pacienteService;

    // 🔴 REGISTRAMOS ESTOS COMPONENTES COMO MOCKS PARA EVITAR EL ERROR "NoSuchBeanDefinitionException"
    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtService jwtService;

    private PacienteResponseDto responseDto;
    private PacienteRequestDto requestDto;

    @BeforeEach
    void setUp() {
        responseDto = new PacienteResponseDto(
                1L, "123456789", "juan@test.com", "Juan", "Perez",
                EstadoPaciente.ACTIVO, LocalDateTime.now()
        );
        requestDto = new PacienteRequestDto("12.345.678-9", "Juan", "Perez", "juan@test.com");
    }

    @Test
    void obtenerTodos_DeberiaRetornarListaYStatus200() throws Exception {
        Mockito.when(pacienteService.obtenerTodos()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    void obtenerPorId_DeberiaRetornarPacienteYStatus200() throws Exception {
        Mockito.when(pacienteService.obtenerPorId(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("123456789"));
    }

    @Test
    void obtenerPorEstado_DeberiaRetornarFiltradosYStatus200() throws Exception {
        Mockito.when(pacienteService.obtenerPorEstado("ACTIVO")).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/pacientes/estado/ACTIVO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    @Test
    void crearPaciente_DeberiaRetornarCreadoYStatus201() throws Exception {
        Mockito.when(pacienteService.crear(any(PacienteRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void actualizarCompleto_DeberiaRetornarActualizadoYStatus200() throws Exception {
        Mockito.when(pacienteService.actualizarCompleto(eq(1L), any(PacienteRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellido").value("Perez"));
    }

    @Test
    void actualizarParcial_DeberiaActualizarCamposYStatus200() throws Exception {
        Map<String, Object> campos = Map.of("nombre", "Pedro");
        Mockito.when(pacienteService.actualizarParcial(eq(1L), any(Map.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/api/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(campos)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_DeberiaBorrarYRetornarStatus204() throws Exception {
        Mockito.doNothing().when(pacienteService).eliminar(1L);

        mockMvc.perform(delete("/api/pacientes/1"))
                .andExpect(status().isNoContent());
    }
}