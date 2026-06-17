package com.example.bff.controller;

import com.example.bff.client.DoctorClient;
import com.example.bff.client.PacienteClient;
import feign.FeignException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/bff/dashboard")
public class BffDashboardController {

    private final PacienteClient pacienteClient;
    private final DoctorClient doctorClient;

    public BffDashboardController(PacienteClient pacienteClient, DoctorClient doctorClient) {
        this.pacienteClient = pacienteClient;
        this.doctorClient = doctorClient;
    }

    // Renamed from /{authId} to avoid collision with literal segments like /patients
    @GetMapping("/perfil/{authId}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public ResponseEntity<Map<String, Object>> obtenerDashboardCompleto(@PathVariable Long authId) {
        Map<String, Object> dashboardResponse = new HashMap<>();

        try {
            ResponseEntity<?> doctorResponse = doctorClient.obtenerPorAuthId(authId);
            dashboardResponse.put("perfilDoctor", doctorResponse.getBody());
        } catch (FeignException e) {
            dashboardResponse.put("perfilDoctor", null);
            dashboardResponse.put("alertaDoctor", "No se pudo cargar el perfil del médico");
        }

        try {
            ResponseEntity<?> pacientesResponse = pacienteClient.getPacientes();
            dashboardResponse.put("listaPacientes", pacientesResponse.getBody());
        } catch (FeignException e) {
            dashboardResponse.put("listaPacientes", null);
            dashboardResponse.put("alertaPacientes", "Los pacientes no están disponibles en este momento");
        }

        return ResponseEntity.ok(dashboardResponse);
    }
}
