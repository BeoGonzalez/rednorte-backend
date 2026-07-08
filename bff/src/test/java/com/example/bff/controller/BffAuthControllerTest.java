package com.example.bff.controller;

import com.example.bff.client.AuthClient;
import com.example.bff.client.DoctorClient;
import com.example.bff.client.PacienteClient;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BffAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class BffAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private AuthClient authClient;
    @MockitoBean private DoctorClient doctorClient;
    @MockitoBean private PacienteClient pacienteClient;

    // ─── POST /bff/auth/register — rol MEDICO orquesta perfil doctor ──
    @Test
    void register_rolMedico_creaPerfilDoctor() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("authId", 5))).when(authClient).register(any());
        doReturn(ResponseEntity.ok("perfil creado")).when(doctorClient).crearPerfil(any());

        String json = """
                {"username":"dr@test.cl","password":"pass","rol":"ROLE_MEDICO",
                 "nombre":"Juan","apellidos":"Pérez","specialty":"Cardiología","registroMedico":"REG-1"}
                """;

        mockMvc.perform(post("/bff/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());

        verify(doctorClient).crearPerfil(any());
        verify(pacienteClient, never()).crearPaciente(any());
    }

    // ─── POST /bff/auth/register — rol PACIENTE orquesta perfil paciente ──
    @Test
    void register_rolPaciente_creaPerfilPaciente() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("authId", 7))).when(authClient).register(any());
        doReturn(ResponseEntity.ok("ok")).when(pacienteClient).crearPaciente(any());

        String json = """
                {"username":"pac@test.cl","password":"pass","rol":"ROLE_PACIENTE",
                 "nombre":"Ana","apellidos":"Soto","rut":"12.345.678-9"}
                """;

        mockMvc.perform(post("/bff/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());

        verify(pacienteClient).crearPaciente(any());
        verify(doctorClient, never()).crearPerfil(any());
    }

    // ─── register — security responde error → se propaga sin orquestar ──
    @Test
    void register_authClientFalla_propagaError() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(400);
        when(e.contentUTF8()).thenReturn("Usuario ya existe");
        when(authClient.register(any())).thenThrow(e);

        mockMvc.perform(post("/bff/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"x\",\"rol\":\"ROLE_PACIENTE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuario ya existe"));

        verify(pacienteClient, never()).crearPaciente(any());
    }

    // ─── register — fallo al crear perfil doctor NO interrumpe la respuesta ──
    @Test
    void register_perfilDoctorFalla_igualRetornaOk() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("authId", 9))).when(authClient).register(any());
        FeignException e = mock(FeignException.class);
        when(doctorClient.crearPerfil(any())).thenThrow(e);

        String json = "{\"username\":\"d@test.cl\",\"rol\":\"ROLE_MEDICO\",\"nombre\":\"J\"}";

        mockMvc.perform(post("/bff/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }

    // ─── POST /bff/auth/login — proxy exitoso ─────────────────────────
    @Test
    void login_exitoso() throws Exception {
        doReturn(ResponseEntity.ok(Map.of("token", "jwt"))).when(authClient).login(any());

        mockMvc.perform(post("/bff/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"x\",\"password\":\"y\"}"))
                .andExpect(status().isOk());

        verify(authClient).login(any());
    }

    // ─── login — credenciales inválidas → propaga error ───────────────
    @Test
    void login_falla_propagaError() throws Exception {
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(401);
        when(e.contentUTF8()).thenReturn("No autorizado");
        when(authClient.login(any())).thenThrow(e);

        mockMvc.perform(post("/bff/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"x\",\"password\":\"bad\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("No autorizado"));
    }
}
