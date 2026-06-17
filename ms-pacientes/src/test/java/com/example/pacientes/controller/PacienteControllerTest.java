package com.example.pacientes.controller;

import com.example.pacientes.dto.NotificacionDto;
import com.example.pacientes.dto.PacienteResponseDto;
import com.example.pacientes.model.EstadoPaciente;
import com.example.pacientes.service.PacienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PacienteController.class)
@AutoConfigureMockMvc(addFilters = false)
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PacienteService service;

    private static final PacienteResponseDto MOCK_DTO = new PacienteResponseDto(
            1L, 10L, "12345678-9", "juan@test.cl", "Juan", "Pérez",
            EstadoPaciente.ACTIVO, LocalDateTime.now()
    );

    // ─── GET /api/pacientes ───────────────────────────────────────
    @Test
    void obtenerTodos_retorna200() throws Exception {
        when(service.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk());

        verify(service).obtenerTodos();
    }

    // ─── GET /api/pacientes/{id} ──────────────────────────────────
    @Test
    void obtenerPorId_existente_retorna200() throws Exception {
        when(service.obtenerPorId(1L)).thenReturn(MOCK_DTO);

        mockMvc.perform(get("/api/pacientes/1"))
                .andExpect(status().isOk());

        verify(service).obtenerPorId(1L);
    }

    // ─── GET /api/pacientes/auth/{authId} ────────────────────────
    @Test
    void obtenerPorAuthId_existente_retorna200() throws Exception {
        when(service.obtenerPorAuthId(10L)).thenReturn(MOCK_DTO);

        mockMvc.perform(get("/api/pacientes/auth/10"))
                .andExpect(status().isOk());

        verify(service).obtenerPorAuthId(10L);
    }

    // ─── GET /api/pacientes/estado/{estado} ──────────────────────
    @Test
    void obtenerPorEstado_retorna200() throws Exception {
        when(service.obtenerPorEstado("ACTIVO")).thenReturn(List.of(MOCK_DTO));

        mockMvc.perform(get("/api/pacientes/estado/ACTIVO"))
                .andExpect(status().isOk());

        verify(service).obtenerPorEstado("ACTIVO");
    }

    // ─── POST /api/pacientes — válido → 201 ──────────────────────
    @Test
    void crearPaciente_payloadValido_retorna201() throws Exception {
        when(service.crear(any())).thenReturn(MOCK_DTO);

        String json = """
                {"authId": 10, "rut": "12.345.678-9", "nombre": "Juan",
                 "apellido": "Pérez", "email": "juan@test.cl"}
                """;

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(service).crear(any());
    }

    // ─── POST /api/pacientes — inválido → 400 ────────────────────
    @Test
    void crearPaciente_payloadInvalido_retorna400() throws Exception {
        String json = "{\"rut\": \"\", \"nombre\": \"\", \"apellido\": \"\", \"email\": \"no-email\"}";

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).crear(any());
    }

    // ─── PUT /api/pacientes/{id} ──────────────────────────────────
    @Test
    void actualizarCompleto_retorna200() throws Exception {
        when(service.actualizarCompleto(eq(1L), any())).thenReturn(MOCK_DTO);

        String json = """
                {"rut": "12.345.678-9", "nombre": "Juan",
                 "apellido": "Pérez", "email": "juan@test.cl"}
                """;

        mockMvc.perform(put("/api/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(service).actualizarCompleto(eq(1L), any());
    }

    // ─── PATCH /api/pacientes/{id} ────────────────────────────────
    @Test
    void actualizarParcial_retorna200() throws Exception {
        when(service.actualizarParcial(eq(1L), any())).thenReturn(MOCK_DTO);

        mockMvc.perform(patch("/api/pacientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Carlos\"}"))
                .andExpect(status().isOk());

        verify(service).actualizarParcial(eq(1L), any());
    }

    // ─── DELETE /api/pacientes/{id} ───────────────────────────────
    @Test
    void eliminar_retorna204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/pacientes/1"))
                .andExpect(status().isNoContent());

        verify(service).eliminar(1L);
    }

    // ─── GET /api/pacientes/{id}/notificaciones ───────────────────
    @Test
    void obtenerNotificaciones_retorna200() throws Exception {
        NotificacionDto notif = new NotificacionDto(1L, 1L, "Cita aceptada", false, null);
        when(service.obtenerNotificaciones(1L)).thenReturn(List.of(notif));

        mockMvc.perform(get("/api/pacientes/1/notificaciones"))
                .andExpect(status().isOk());

        verify(service).obtenerNotificaciones(1L);
    }
}
