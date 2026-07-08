package com.example.bff.controller;

import com.example.bff.client.DoctorClient;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BffDoctorController.class)
@AutoConfigureMockMvc(addFilters = false)
class BffDoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorClient doctorClient;

    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void testObtenerPorAuthId_ServicioRespondeOk() throws Exception {
        // 🟢 SOLUCIÓN: Usar doReturn
        doReturn(ResponseEntity.ok("Datos del Doctor")).when(doctorClient).obtenerPorAuthId(1L);

        mockMvc.perform(get("/bff/doctores/auth/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Datos del Doctor"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void testObtenerPorAuthId_ServicioRespondeError_ProxyPropagaStatus() throws Exception {
        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(404);
        when(feignException.contentUTF8()).thenReturn("Doctor no encontrado en la BD");

        when(doctorClient.obtenerPorAuthId(99L)).thenThrow(feignException);

        mockMvc.perform(get("/bff/doctores/auth/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Doctor no encontrado en la BD"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_PACIENTE")
    void testObtenerPorEspecialidad_ServicioRespondeOk() throws Exception {
        doReturn(ResponseEntity.ok(java.util.List.of())).when(doctorClient).obtenerPorEspecialidad("Cardiología");

        mockMvc.perform(get("/bff/doctores/especialidad/Cardiología"))
                .andExpect(status().isOk());

        verify(doctorClient).obtenerPorEspecialidad("Cardiología");
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void testObtenerPorEspecialidad_ServicioError_ProxyPropagaStatus() throws Exception {
        FeignException feignException = mock(FeignException.class);
        when(feignException.status()).thenReturn(503);
        when(feignException.contentUTF8()).thenReturn("Servicio no disponible");
        when(doctorClient.obtenerPorEspecialidad("Neurología")).thenThrow(feignException);

        mockMvc.perform(get("/bff/doctores/especialidad/Neurología"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Servicio no disponible"));
    }
}