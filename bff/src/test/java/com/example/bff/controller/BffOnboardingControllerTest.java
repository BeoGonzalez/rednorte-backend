package com.example.bff.controller;

import com.example.bff.client.DoctorClient;
import com.example.bff.client.PacienteClient;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BffOnboardingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BffOnboardingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private PacienteClient pacienteClient;
    @MockitoBean private DoctorClient doctorClient;

    // ─── GET perfil-paciente ──────────────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void getPerfilPaciente_ok() throws Exception {
        doReturn(ResponseEntity.ok("perfil")).when(pacienteClient).obtenerPorAuthId(1L);

        mockMvc.perform(get("/api/bff/onboarding/perfil-paciente/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void getPerfilPaciente_notFound_retorna404() throws Exception {
        FeignException.NotFound nf = mock(FeignException.NotFound.class);
        when(pacienteClient.obtenerPorAuthId(2L)).thenThrow(nf);

        mockMvc.perform(get("/api/bff/onboarding/perfil-paciente/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void getPerfilPaciente_otroError_propaga() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(500);
        when(e.contentUTF8()).thenReturn("boom");
        when(pacienteClient.obtenerPorAuthId(3L)).thenThrow(e);

        mockMvc.perform(get("/api/bff/onboarding/perfil-paciente/3"))
                .andExpect(status().isInternalServerError());
    }

    // ─── GET perfil-medico ────────────────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void getPerfilMedico_ok() throws Exception {
        doReturn(ResponseEntity.ok("perfil")).when(doctorClient).obtenerPorAuthId(1L);

        mockMvc.perform(get("/api/bff/onboarding/perfil-medico/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void getPerfilMedico_notFound_retorna404() throws Exception {
        FeignException.NotFound nf = mock(FeignException.NotFound.class);
        when(doctorClient.obtenerPorAuthId(2L)).thenThrow(nf);

        mockMvc.perform(get("/api/bff/onboarding/perfil-medico/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void getPerfilMedico_otroError_propaga() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(502);
        when(e.contentUTF8()).thenReturn("bad gateway");
        when(doctorClient.obtenerPorAuthId(3L)).thenThrow(e);

        mockMvc.perform(get("/api/bff/onboarding/perfil-medico/3"))
                .andExpect(status().isBadGateway());
    }

    // ─── POST crear perfil paciente ───────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void crearPerfilPaciente_ok() throws Exception {
        doReturn(ResponseEntity.ok("creado")).when(pacienteClient).crearPaciente(any());

        mockMvc.perform(post("/api/bff/onboarding/paciente")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"rut\":\"1-9\"}"))
                .andExpect(status().isOk());

        verify(pacienteClient).crearPaciente(any());
    }

    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void crearPerfilPaciente_error_propaga() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(409);
        when(e.contentUTF8()).thenReturn("duplicado");
        when(pacienteClient.crearPaciente(any())).thenThrow(e);

        mockMvc.perform(post("/api/bff/onboarding/paciente")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isConflict());
    }

    // ─── POST crear perfil medico ─────────────────────────────────
    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void crearPerfilMedico_ok() throws Exception {
        doReturn(ResponseEntity.ok("creado")).when(doctorClient).crearPerfil(any());

        mockMvc.perform(post("/api/bff/onboarding/medico")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"J\"}"))
                .andExpect(status().isOk());

        verify(doctorClient).crearPerfil(any());
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void crearPerfilMedico_error_propaga() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(400);
        when(e.contentUTF8()).thenReturn("invalido");
        when(doctorClient.crearPerfil(any())).thenThrow(e);

        mockMvc.perform(post("/api/bff/onboarding/medico")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
