package com.example.ms_doctores.controller;

import com.example.ms_doctores.dto.DoctorRequestDTO;
import com.example.ms_doctores.entity.Doctor;
import com.example.ms_doctores.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
@AutoConfigureMockMvc(addFilters = false)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService service;

    private static final Doctor DOCTOR_MOCK;
    static {
        DOCTOR_MOCK = new Doctor();
        DOCTOR_MOCK.setId(1L);
        DOCTOR_MOCK.setAuthId(123L);
        DOCTOR_MOCK.setNombre("Juan");
        DOCTOR_MOCK.setApellidos("Pérez");
        DOCTOR_MOCK.setEspecialidad("Cardiología");
        DOCTOR_MOCK.setRegistroMedico("REG-999");
    }

    // ─── POST /api/doctores/perfil ────────────────────────────────
    @Test
    void crearDoctor_exitoso_retorna200() throws Exception {
        when(service.registrarDoctor(any(DoctorRequestDTO.class))).thenReturn(DOCTOR_MOCK);

        String json = """
                {"authId": 123, "nombre": "Juan", "apellidos": "Pérez",
                 "specialty": "Cardiología", "registroMedico": "REG-999"}
                """;

        mockMvc.perform(post("/api/doctores/perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(service).registrarDoctor(any(DoctorRequestDTO.class));
    }

    // ─── GET /api/doctores/auth/{authId} ──────────────────────────
    @Test
    void obtenerDoctorPorAuthId_existente_retorna200() throws Exception {
        when(service.buscarPorAuthId(123L)).thenReturn(Optional.of(DOCTOR_MOCK));

        mockMvc.perform(get("/api/doctores/auth/123"))
                .andExpect(status().isOk());

        verify(service).buscarPorAuthId(123L);
    }

    @Test
    void obtenerDoctorPorAuthId_noExistente_retorna404() throws Exception {
        when(service.buscarPorAuthId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/doctores/auth/99"))
                .andExpect(status().isNotFound());
    }
}
