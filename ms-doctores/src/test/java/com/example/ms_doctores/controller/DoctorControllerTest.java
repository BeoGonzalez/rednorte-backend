package com.example.ms_doctores.controller;

import com.example.ms_doctores.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
@AutoConfigureMockMvc(addFilters = false)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService service;

    @Test
    void testCrearDoctor_PayloadInvalido_Retorna400() throws Exception {
        // Simulamos un JSON incompleto o inválido
        String jsonInvalido = "{\"nombre\": \"\", \"especialidad\": \"\"}";

        mockMvc.perform(post("/api/doctores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }
}