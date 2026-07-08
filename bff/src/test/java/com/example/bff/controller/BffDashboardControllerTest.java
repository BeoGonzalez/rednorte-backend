package com.example.bff.controller;

import com.example.bff.client.DoctorClient;
import com.example.bff.client.PacienteClient;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BffDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class BffDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PacienteClient pacienteClient;

    @MockitoBean
    private DoctorClient doctorClient;

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void testDashboard_AmbosServiciosResponden_Exito() throws Exception {
        // 🟢 SOLUCIÓN: Usar doReturn en lugar de thenReturn para evadir el comodín <?>
        doReturn(ResponseEntity.ok("Perfil Medico OK")).when(doctorClient).obtenerPorAuthId(1L);
        doReturn(ResponseEntity.ok(Collections.emptyList())).when(pacienteClient).getPacientes();

        mockMvc.perform(get("/api/bff/dashboard/perfil/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfilDoctor").value("Perfil Medico OK"))
                .andExpect(jsonPath("$.listaPacientes").isArray());
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void testDashboard_MicroservicioDoctorCaido_RetornaAlerta() throws Exception {
        FeignException feignException = mock(FeignException.class);
        when(doctorClient.obtenerPorAuthId(1L)).thenThrow(feignException);

        // 🟢 SOLUCIÓN: Usar doReturn
        doReturn(ResponseEntity.ok(Collections.emptyList())).when(pacienteClient).getPacientes();

        mockMvc.perform(get("/api/bff/dashboard/perfil/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfilDoctor").doesNotExist())
                .andExpect(jsonPath("$.alertaDoctor").value("No se pudo cargar el perfil del médico"))
                .andExpect(jsonPath("$.listaPacientes").isArray());
    }

    @Test
    @WithMockUser(authorities = "ROLE_MEDICO")
    void testDashboard_MicroservicioPacientesCaido_RetornaAlerta() throws Exception {
        // 🟢 SOLUCIÓN: Usar doReturn
        doReturn(ResponseEntity.ok("Perfil Medico OK")).when(doctorClient).obtenerPorAuthId(1L);

        FeignException feignException = mock(FeignException.class);
        when(pacienteClient.getPacientes()).thenThrow(feignException);

        mockMvc.perform(get("/api/bff/dashboard/perfil/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfilDoctor").value("Perfil Medico OK"))
                .andExpect(jsonPath("$.listaPacientes").doesNotExist())
                .andExpect(jsonPath("$.alertaPacientes").value("Los pacientes no están disponibles en este momento"));
    }
}