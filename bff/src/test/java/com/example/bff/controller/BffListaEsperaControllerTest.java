package com.example.bff.controller;

import com.example.bff.client.ListaEsperaClient;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BffListaEsperaController.class)
@AutoConfigureMockMvc(addFilters = false)
class BffListaEsperaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListaEsperaClient listaEsperaClient;

    // ─── GET /bff/lista-espera ────────────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void obtenerListaPriorizada_ok() throws Exception {
        doReturn(ResponseEntity.ok(Collections.emptyList())).when(listaEsperaClient).obtenerListaPriorizada();

        mockMvc.perform(get("/bff/lista-espera"))
                .andExpect(status().isOk());

        verify(listaEsperaClient).obtenerListaPriorizada();
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void obtenerListaPriorizada_servicioCaido_propagaError() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(503);
        when(e.contentUTF8()).thenReturn("Servicio no disponible");
        when(listaEsperaClient.obtenerListaPriorizada()).thenThrow(e);

        mockMvc.perform(get("/bff/lista-espera"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Servicio no disponible"));
    }

    // ─── GET /bff/lista-espera/{id} ───────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void obtenerPorId_ok() throws Exception {
        doReturn(ResponseEntity.ok("detalle")).when(listaEsperaClient).obtenerPorId(eq(1L), anyString());

        mockMvc.perform(get("/bff/lista-espera/1").header("Authorization", "Bearer t"))
                .andExpect(status().isOk());

        verify(listaEsperaClient).obtenerPorId(eq(1L), anyString());
    }

    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void obtenerPorId_error_propaga() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(404);
        when(e.contentUTF8()).thenReturn("No encontrado");
        when(listaEsperaClient.obtenerPorId(eq(9L), anyString())).thenThrow(e);

        mockMvc.perform(get("/bff/lista-espera/9").header("Authorization", "Bearer t"))
                .andExpect(status().isNotFound());
    }

    // ─── POST /bff/lista-espera ───────────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void registrarSolicitud_ok() throws Exception {
        doReturn(ResponseEntity.ok("creada")).when(listaEsperaClient).registrarSolicitud(any(), anyString());

        mockMvc.perform(post("/bff/lista-espera")
                        .header("Authorization", "Bearer t")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pacienteId\":10}"))
                .andExpect(status().isOk());

        verify(listaEsperaClient).registrarSolicitud(any(), anyString());
    }

    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void registrarSolicitud_error_propaga() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(400);
        when(e.contentUTF8()).thenReturn("Datos inválidos");
        when(listaEsperaClient.registrarSolicitud(any(), anyString())).thenThrow(e);

        mockMvc.perform(post("/bff/lista-espera")
                        .header("Authorization", "Bearer t")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ─── GET /bff/lista-espera/filtrar ────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void filtrar_ok() throws Exception {
        doReturn(ResponseEntity.ok(List.of())).when(listaEsperaClient).filtrar("PENDIENTE", "GENERAL");

        mockMvc.perform(get("/bff/lista-espera/filtrar")
                        .param("estado", "PENDIENTE").param("tipoCita", "GENERAL"))
                .andExpect(status().isOk());

        verify(listaEsperaClient).filtrar("PENDIENTE", "GENERAL");
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void filtrar_error_propaga() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(500);
        when(e.contentUTF8()).thenReturn("Error");
        when(listaEsperaClient.filtrar(anyString(), anyString())).thenThrow(e);

        mockMvc.perform(get("/bff/lista-espera/filtrar")
                        .param("estado", "X").param("tipoCita", "Y"))
                .andExpect(status().isInternalServerError());
    }

    // ─── GET /bff/lista-espera/estado/{estado} ────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void obtenerPorEstado_ok() throws Exception {
        doReturn(ResponseEntity.ok(List.of())).when(listaEsperaClient).obtenerPorEstado("PENDIENTE");

        mockMvc.perform(get("/bff/lista-espera/estado/PENDIENTE"))
                .andExpect(status().isOk());

        verify(listaEsperaClient).obtenerPorEstado("PENDIENTE");
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void obtenerPorEstado_error_propaga() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(503);
        when(e.contentUTF8()).thenReturn("caído");
        when(listaEsperaClient.obtenerPorEstado("PENDIENTE")).thenThrow(e);

        mockMvc.perform(get("/bff/lista-espera/estado/PENDIENTE"))
                .andExpect(status().isServiceUnavailable());
    }

    // ─── PATCH /bff/lista-espera/{id} ─────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void actualizarParcial_ok() throws Exception {
        doReturn(ResponseEntity.ok("ok")).when(listaEsperaClient).actualizarParcial(eq(1L), any());

        mockMvc.perform(patch("/bff/lista-espera/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"ACEPTADA\"}"))
                .andExpect(status().isOk());

        verify(listaEsperaClient).actualizarParcial(eq(1L), any());
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void actualizarParcial_error_propaga() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(404);
        when(e.contentUTF8()).thenReturn("no existe");
        when(listaEsperaClient.actualizarParcial(eq(9L), any())).thenThrow(e);

        mockMvc.perform(patch("/bff/lista-espera/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }
}
