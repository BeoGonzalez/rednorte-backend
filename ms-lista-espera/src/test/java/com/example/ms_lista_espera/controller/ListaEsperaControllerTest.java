package com.example.ms_lista_espera.controller;

import com.example.ms_lista_espera.dto.RegistroSolicitudDto;
import com.example.ms_lista_espera.dto.SolicitudResponseDto;
import com.example.ms_lista_espera.service.ListaEsperaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListaEsperaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ListaEsperaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListaEsperaService listaEsperaService;

    private static final SolicitudResponseDto MOCK_SOL = new SolicitudResponseDto(
            1L, 10L, "12345678-9", "Juan Pérez",
            "MEDICINA_GENERAL", "ALTA", "BUSCANDO_CITA", null, null
    );

    // ─── GET /api/listas-espera ───────────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void obtenerListaPriorizada_retorna200() throws Exception {
        when(listaEsperaService.obtenerListaPriorizada()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/listas-espera"))
                .andExpect(status().isOk());

        verify(listaEsperaService).obtenerListaPriorizada();
    }

    // ─── GET /api/listas-espera/{id} ─────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void obtenerPorId_existente_retorna200() throws Exception {
        when(listaEsperaService.obtenerPorId(eq(1L), anyString())).thenReturn(MOCK_SOL);

        mockMvc.perform(get("/api/listas-espera/1")
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk());

        verify(listaEsperaService).obtenerPorId(eq(1L), anyString());
    }

    // ─── GET /api/listas-espera/filtrar ──────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void filtrar_retorna200() throws Exception {
        when(listaEsperaService.filtrar(anyString(), anyString(), anyString()))
                .thenReturn(List.of(MOCK_SOL));

        mockMvc.perform(get("/api/listas-espera/filtrar")
                        .param("estado", "BUSCANDO_CITA")
                        .param("tipoCita", "MEDICINA_GENERAL")
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk());

        verify(listaEsperaService).filtrar(anyString(), anyString(), anyString());
    }

    // ─── GET /api/listas-espera/estado/{estado} ───────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void obtenerPorEstado_retorna200() throws Exception {
        when(listaEsperaService.obtenerPorEstado("BUSCANDO_CITA")).thenReturn(List.of(MOCK_SOL));

        mockMvc.perform(get("/api/listas-espera/estado/BUSCANDO_CITA"))
                .andExpect(status().isOk());

        verify(listaEsperaService).obtenerPorEstado("BUSCANDO_CITA");
    }

    // ─── POST /api/listas-espera/registro ────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void registrar_exitoso_retorna201() throws Exception {
        when(listaEsperaService.registrarSolicitud(any(RegistroSolicitudDto.class), anyString()))
                .thenReturn(MOCK_SOL);

        String json = "{\"pacienteId\": 10, \"tipoSolicitud\": \"MEDICINA_GENERAL\", \"gravedad\": \"ALTA\"}";

        mockMvc.perform(post("/api/listas-espera/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isCreated());

        verify(listaEsperaService).registrarSolicitud(any(RegistroSolicitudDto.class), anyString());
    }

    // ─── PUT /api/listas-espera/{id} ─────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void actualizarCompleto_retorna200() throws Exception {
        when(listaEsperaService.actualizarCompleto(eq(1L), any(RegistroSolicitudDto.class)))
                .thenReturn(MOCK_SOL);

        String json = "{\"pacienteId\": 10, \"tipoSolicitud\": \"NEUROLOGIA\", \"gravedad\": \"BAJA\"}";

        mockMvc.perform(put("/api/listas-espera/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(listaEsperaService).actualizarCompleto(eq(1L), any(RegistroSolicitudDto.class));
    }

    // ─── PATCH /api/listas-espera/{id} ───────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void actualizarParcial_retorna200() throws Exception {
        when(listaEsperaService.actualizarParcial(eq(1L), any())).thenReturn(MOCK_SOL);

        mockMvc.perform(patch("/api/listas-espera/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": \"ACEPTADA\", \"doctorId\": 5}"))
                .andExpect(status().isOk());

        verify(listaEsperaService).actualizarParcial(eq(1L), any());
    }

    // ─── DELETE /api/listas-espera/{id} ──────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void eliminar_retorna204() throws Exception {
        doNothing().when(listaEsperaService).eliminar(1L);

        mockMvc.perform(delete("/api/listas-espera/1"))
                .andExpect(status().isNoContent());

        verify(listaEsperaService).eliminar(1L);
    }
}
